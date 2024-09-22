package com.borrowbook.duyanh.service;

import com.borrowbook.duyanh.dto.request.BorrowDTO;
import com.borrowbook.duyanh.dto.response.PageResponse;
import com.borrowbook.duyanh.entity.Book;
import com.borrowbook.duyanh.entity.Borrow;
import com.borrowbook.duyanh.entity.BorrowDetail;
import com.borrowbook.duyanh.entity.User;
import com.borrowbook.duyanh.exception.AppException;
import com.borrowbook.duyanh.exception.ErrorCode;
import com.borrowbook.duyanh.repository.BorrowDetailRepository;
import com.borrowbook.duyanh.repository.BorrowRepository;
import com.borrowbook.duyanh.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class BorrowServiceImpl implements BorrowService {

    private final BorrowRepository borrowRepository;

    private final UserService userService;
    @Autowired
    private UserRepository userRepository;

    private final BorrowDetailRepository borrowDetailRepository;

    private final BookService bookService;

    private final JavaMailSender mailSender;

    @Autowired
    public BorrowServiceImpl(BorrowRepository borrowRepository,
                             UserService userService,
                             BorrowDetailRepository borrowDetailRepository,
                             BookService bookService,
                             JavaMailSender mailSender) {
        this.borrowRepository = borrowRepository;
        this.userService = userService;
        this.borrowDetailRepository = borrowDetailRepository;
        this.bookService = bookService;
        this.mailSender = mailSender;
    }
    private void checkBookLimit(Map<Integer, Integer> books) {
        int totalBooks = books.values().stream().mapToInt(Integer::intValue).sum();

        if (totalBooks > 5) {
            throw new AppException(ErrorCode.BOOK_LIMIT_EXCEEDED);
        }
    }

    private void checkOngoingBorrowings(User user) {

        boolean hasOngoingBorrowings = borrowRepository.existsByUserAndStatus(user, "BORROW");

        if (hasOngoingBorrowings) {
            throw new AppException(ErrorCode.ONGOING_BORROWINGS);
        }
    }


    @Override
    @Transactional
    public Borrow borrowingBook(BorrowDTO dto) {
        LocalDate date = LocalDate.now();
        Borrow borrow = new Borrow();
        checkBookLimit(dto.getBooks());
        checkOngoingBorrowings(userRepository.findById(dto.getUserId()).orElseThrow(
                () -> new RuntimeException(ErrorCode.NOT_FOUND.getMessage())));

        User user = userRepository.findById(dto.getUserId()).orElseThrow(
                () -> new AppException(ErrorCode.NOT_FOUND)
        );

        if ("BAN".equalsIgnoreCase(user.getStatus())) {
            throw new AppException(ErrorCode.USER_BANNED);
        }

        borrow.setBorrowDate(date);
        borrow.setStatus("BORROW");
        borrow.setExpirationDate(dto.getExpirationDate());
        borrow.setUser(user);

        Borrow savedBorrow = borrowRepository.save(borrow);

        for (Map.Entry<Integer, Integer> entry : dto.getBooks().entrySet()) {
            int bookId = entry.getKey();
            int quantity = entry.getValue();

            Book book = bookService.getBookById(bookId);
            if (book.getQuantity() < quantity) {
                throw new AppException(ErrorCode.ENOUGH_BOOK);
            }

            BorrowDetail borrowDetail = new BorrowDetail();
            borrowDetail.setBorrow(savedBorrow);
            borrowDetail.setBook(book);
            borrowDetail.setQuantity(quantity);
            borrowDetail.setBookName(book.getBookName());
            borrowDetail.setCompositionPrice(book.getPrice());
            borrowDetail.setDescription(dto.getDescription());
            borrowDetail.setStatus("BORROW");

            borrowDetailRepository.save(borrowDetail);

            book.setQuantity(book.getQuantity() - quantity);
        }

        return savedBorrow;
    }

    @Override
    public List<Borrow> getAllBorrowActiveByUserId(int uid) {
        return borrowRepository.getAllBorrowActiveByUserId(uid);
    }

    @Override
    public Borrow getBorrowById(int bid) {
        return borrowRepository.findById(bid).orElseThrow(
                () -> new RuntimeException(ErrorCode.NOT_FOUND.getMessage()));
    }

    @Override
    public List<Borrow> getHistoryBorrowByUserId(int id) {
        return borrowRepository.getHistoryBorrowByUserId(id);
    }

    @Override
    public PageResponse<Borrow> getBorrowActive(int page, int size, String sortOrder) {
        Sort sort = Sort.by("expiration_date");
        if ("DESC".equalsIgnoreCase(sortOrder)) {
            sort = sort.descending();
        } else {
            sort = sort.ascending();
        }

        Pageable pageable = PageRequest.of(page - 1, size, sort);

        Page<Borrow> pageData = borrowRepository.getBorrowActive(pageable);

        return PageResponse.<Borrow>builder()
                .currentPage(page)
                .pageSize(pageData.getSize())
                .totalPages(pageData.getTotalPages())
                .totalElement(pageData.getTotalElements())
                .data(pageData.getContent())
                .build();
    }

    @Scheduled(cron = "0 0 0 * * *")
    private void scanBorrowsForExpiration() {
        LocalDate today = LocalDate.now();

        // Trước hạn 3 ngày sẽ gửi mail để remind
        LocalDate warningDate = today.plusDays(3);

        List<Borrow> borrows = borrowRepository.findBorrowsNearExpiration(warningDate);
        for (Borrow b : borrows) {
            try {
                sendReminderEmail(b.getUser().getInformationOfUser().getEmail(), b, false);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }

        List<BorrowDetail> overdueBorrows = borrowRepository.findOverdueBorrows(today);
        for (Borrow b : borrows) {
            try {
                sendReminderEmail(b.getUser().getInformationOfUser().getEmail(), b, true);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }
    }

    @Transactional
    private void sendReminderEmail(String email, Borrow borrow, boolean isOverdue) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        List<BorrowDetail> updatedDetails = new ArrayList<>();
        List<BorrowDetail> borrowDetail = borrowDetailRepository.getBorrowDetailBorrowByBorrowId(borrow.getId());

        helper.setTo(email);
        helper.setSubject(isOverdue ? "Sách quá hạn" : "Nhắc nhở trả sách");

        StringBuilder emailContent = new StringBuilder();
        emailContent.append("Xin chào, \n\n");
        BigDecimal totalLateFee = (borrow.getTotalCompositionPrice() == null) ? BigDecimal.ZERO : borrow.getTotalCompositionPrice();

        if (isOverdue) {
            emailContent.append("Các quyển sách của bạn đã quá hạn. Dưới đây là thông tin chi tiết:\n\n");
        } else {
            emailContent.append("Các quyển sách của bạn sắp đến hạn trả. Dưới đây là thông tin chi tiết:\n\n");
        }

        for (BorrowDetail detail : borrowDetail) {
            LocalDate expirationDate = detail.getBorrow().getExpirationDate();
            long daysLate = isOverdue ? expirationDate.until(LocalDate.now()).getDays() : 0;
            BigDecimal lateFee = isOverdue ? BigDecimal.valueOf(10000L).multiply(BigDecimal.valueOf(daysLate)).multiply(BigDecimal.valueOf(detail.getQuantity())) : BigDecimal.ZERO;
            detail.setCompositionPrice(lateFee);
            updatedDetails.add(detail);

            emailContent.append("Sách: ").append(detail.getBookName()).append("\n");
            emailContent.append("Số lượng: ").append(detail.getQuantity()).append("\n");

            if (isOverdue) {
                emailContent.append("Sách đã quá hạn ").append(daysLate).append(" ngày.\n");
                emailContent.append("Giá phạt: ").append(lateFee).append(" VND.\n\n");
            }
            totalLateFee = totalLateFee.add(lateFee);
        }

        emailContent.append("Ngày thuê: ").append(borrow.getBorrowDate()).append("\n");
        emailContent.append("Hạn trả: ").append(borrow.getExpirationDate()).append("\n");

        if (isOverdue) {
            emailContent.append("Tổng giá phạt: ").append(totalLateFee).append(" VND.\n");
        }

        emailContent.append("\nVui lòng trả sách đúng hạn.\nXin cảm ơn!");

        borrowDetailRepository.saveAll(updatedDetails);
        borrow.setTotalCompositionPrice(totalLateFee);
        borrowRepository.save(borrow);

        helper.setText(emailContent.toString());
        mailSender.send(message);
    }
}
