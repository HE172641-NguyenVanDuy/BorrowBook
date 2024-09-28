package com.borrowbook.duyanh.utils;

import com.borrowbook.duyanh.configuration.Translator;
import com.borrowbook.duyanh.dto.response.ApiResponse;
import com.borrowbook.duyanh.entity.InformationOfUser;
import com.borrowbook.duyanh.entity.Role;
import com.borrowbook.duyanh.entity.User;
import com.borrowbook.duyanh.exception.ErrorCode;
import com.borrowbook.duyanh.repository.InformationOfUserRepository;
import com.borrowbook.duyanh.repository.RoleRepository;
import com.borrowbook.duyanh.repository.UserRepository;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.borrowbook.duyanh.utils.Constants.*;

@Component
public class UtilsExcel {

    private static final Logger log = LoggerFactory.getLogger(UtilsExcel.class);
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private InformationOfUserRepository informationOfUserRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Value("${defaultPassword}")
    String defaultPassword;

    public UtilsExcel() {
    }

    public void generateExcel(HttpServletResponse response, boolean isSearch, String keyword) throws IOException {
        setUpResponse(response);
        List<User> usersList;
        if(!isSearch && keyword == null) {
            usersList = userRepository.findAll();
        } else {
            usersList = userRepository.searchAdvancedUser(keyword);
        }

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet(Translator.toLocale("sheet.name"));
        XSSFRow row = sheet.createRow(0);

        row.createCell(0).setCellValue(Translator.toLocale("username.title"));
        row.createCell(1).setCellValue(Translator.toLocale("role.title"));
        row.createCell(2).setCellValue(Translator.toLocale("email.title"));
        row.createCell(3).setCellValue(Translator.toLocale("phone.number.title"));
        row.createCell(4).setCellValue(Translator.toLocale("dob.title"));
        row.createCell(5).setCellValue(Translator.toLocale("status.title"));

        int dataRowIndex = 1;
        for (User u : usersList) {
            XSSFRow dataRow = sheet.createRow(dataRowIndex);
            dataRow.createCell(0).setCellValue(u.getUsername());
            dataRow.createCell(1).setCellValue(u.getRole().getRoleName());
            InformationOfUser informationOfUser = informationOfUserRepository.findById(u.getId()).orElseThrow(
                    () -> new RuntimeException(ErrorCode.NOT_FOUND.getMessage())
            );
            if (informationOfUser != null) {
                dataRow.createCell(2).setCellValue(informationOfUser.getEmail());
                dataRow.createCell(3).setCellValue(informationOfUser.getPhoneNumber());
                dataRow.createCell(4).setCellValue(informationOfUser.getDob() != null ? informationOfUser.getDob().toString() : "N/A");
            } else {
                dataRow.createCell(2).setCellValue("N/A");
                dataRow.createCell(3).setCellValue("N/A");
                dataRow.createCell(4).setCellValue("N/A");
            }

            dataRow.createCell(5).setCellValue(u.getStatus());
            dataRowIndex++;
        }

        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
    }

    public void setUpResponse(HttpServletResponse response) {
        response.setContentType("application/octet-stream");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment;filename=users.xlsx";
        response.setHeader(headerKey, headerValue);
    }

    public ApiResponse<String> getTemporaryFileInServer(MultipartFile file, HttpServletResponse response) {
        String msg = "";
        try {
            String tempDir = System.getProperty("java.io.tmpdir");
            String filePath = tempDir + java.io.File.separator + UUID.randomUUID() + "_" + file.getOriginalFilename();

            java.io.File tempFile = new java.io.File(filePath);
            file.transferTo(tempFile);

            importExcel(response, filePath);

            if (tempFile.exists()) {
                tempFile.delete();
            }
            msg = Translator.toLocale("upload.success");
        } catch (IOException e) {
            msg = Translator.toLocale("error.upload") + e.getMessage();
        }
        return ApiResponse.<String>builder()
                .code(200)
                .message(msg)
                .build();
    }

    @Transactional
    public void importExcel(HttpServletResponse response, String filePath) {
        HashMap<String, List<Integer>> emailMap = new HashMap<>();
        HashMap<String, List<Integer>> usernameMap = new HashMap<>();
        HashMap<String, List<Integer>> phoneNumberMap = new HashMap<>();
        List<String> existingPhoneNumber = informationOfUserRepository.findAllPhoneNumber();
        List<String> existingUsername = userRepository.findAllUsername();
        List<String> existingEmails = informationOfUserRepository.findAllEmail();
        boolean hasDuplicates = false;

        try (FileInputStream fis = new FileInputStream(filePath)) {
            Workbook workbook = new XSSFWorkbook(fis);
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if (row.getRowNum() == 0) {
                    continue;
                }

                Cell usernameCell = row.getCell(0);
                Cell emailCell = row.getCell(3);
                if ((usernameCell == null || usernameCell.getCellType() == CellType.BLANK) &&
                        (emailCell == null || emailCell.getCellType() == CellType.BLANK)) {
                    break;
                }
                String errorMsg = validateRow(row, existingEmails, emailMap,existingUsername
                        ,usernameMap,existingPhoneNumber,phoneNumberMap);

                Cell errorCell = row.createCell(8);
                errorCell.setCellValue(errorMsg);

                CellStyle style = row.getSheet().getWorkbook().createCellStyle();
                style.setWrapText(true);
                errorCell.setCellStyle(style);
                if (!errorMsg.isEmpty()) {
                    hasDuplicates = true;
                }
            }
            if (hasDuplicates) {
                exportExcelWithDuplicates(response, workbook);
            } else {
                saveExcelData(sheet);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private String validateRow(Row row,
                               List<String> existingEmails,
                               HashMap<String, List<Integer>> emailMap,
                               List<String> existingUsername,
                               HashMap<String, List<Integer>> usernameMap,
                                List<String> existingPhoneNumber,
                                HashMap<String, List<Integer>> phoneNumberMap )throws Exception {
        StringBuilder errorMsg = new StringBuilder();

        Cell usernameCell = row.getCell(0);
        if (usernameCell != null && usernameCell.getCellType() == CellType.STRING) {
            String username = usernameCell.getStringCellValue();
            if (!checkUsername(username)) {
                errorMsg.append(Translator.toLocale("username.wrong") + "\r\n");
            } else {
                if (existingUsername.contains(username)) {
                    errorMsg.append(Translator.toLocale("username.existed") + "\r\n");
                }
                if (usernameMap.containsKey(username)) {
                    errorMsg.append(Translator.toLocale("username.existed.row") + "\r\n");
                    errorMsg.append(usernameMap.get(username).toString());
                    errorMsg.append("\n");
                } else {
                    usernameMap.put(username, new ArrayList<>());
                }

                usernameMap.get(username).add(row.getRowNum());
            }
        } else {
            errorMsg.append(Translator.toLocale("username.null") + "\r\n");
        }

        Cell passwordCell = row.getCell(1);
        if (passwordCell != null) {
            String password = "";
            if (passwordCell.getCellType() == CellType.STRING) {
                password = passwordCell.getStringCellValue();
                log.info("Password is :" + password);
                if (!checkPassword(password)) {
                    errorMsg.append(Translator.toLocale("password.wrong") + "\r\n");
                }
            } else if (passwordCell.getCellType() == CellType.NUMERIC) {
                password = String.valueOf((long) passwordCell.getNumericCellValue());
                log.info("Password is :" + password);
                if (!checkPassword(password)) {
                    errorMsg.append(Translator.toLocale("password.wrong") + "\r\n");
                }
            }
        } else log.warn(Translator.toLocale("password.null") );

        Cell phoneNumberCell = row.getCell(2);
        if (phoneNumberCell != null && phoneNumberCell.getCellType() == CellType.STRING) {
            String phoneNumber = usernameCell.getStringCellValue();
            if (!checkPhoneNumber(phoneNumber)) {
                errorMsg.append(Translator.toLocale("phone.number.wrong") + "\r\n");
            } else {
                if (existingPhoneNumber.contains(phoneNumber)) {
                    errorMsg.append(Translator.toLocale("phone.number.existed") + "\r\n");
                }
                if (phoneNumberMap.containsKey(phoneNumber)) {
                    errorMsg.append(Translator.toLocale("phone.number.existed.row") + "\r\n");
                    errorMsg.append(phoneNumberMap.get(phoneNumber).toString());
                    errorMsg.append("\n");
                } else {
                    phoneNumberMap.put(phoneNumber, new ArrayList<>());
                }

                phoneNumberMap.get(phoneNumber).add(row.getRowNum());
            }
        } else {
            errorMsg.append(Translator.toLocale("phone.number.null"));
        }

        Cell emailCell = row.getCell(3);
        if (emailCell != null && emailCell.getCellType() == CellType.STRING) {
            String email = emailCell.getStringCellValue();

            if (!checkEmail(email)) {
                errorMsg.append(Translator.toLocale("email.wrong") + "\r\n");
            } else {
                if (existingEmails.contains(email)) {
                    errorMsg.append(Translator.toLocale("email.number.existed") + "\r\n");
                }

                if (emailMap.containsKey(email)) {
                    errorMsg.append(Translator.toLocale("email.number.existed.row") + "\r\n");
                    errorMsg.append(emailMap.get(email).toString());
                    errorMsg.append("\n");
                } else {
                    emailMap.put(email, new ArrayList<>());
                }

                emailMap.get(email).add(row.getRowNum());
            }
        } else {
            errorMsg.append(Translator.toLocale("email.number.null") + "\r\n");
        }

        Cell dobCell = row.getCell(4);
        if (dobCell != null) {
            if (dobCell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(dobCell)) {
                Date dobDate = dobCell.getDateCellValue();
                log.info("Dob is: " + dobDate.toString());
                if (!validateDateOfBirth(dobDate)) {
                    errorMsg.append(Translator.toLocale("dob.wrong") + "\r\n");
                }
            }
            if (dobCell.getCellType() == CellType.STRING) {
                try {
                    String dobString = dobCell.getStringCellValue();
                    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    Date dobDate = dateFormat.parse(dobString);
                    log.info(dobDate.toString());
                    if (!validateDateOfBirth(dobDate)) {
                        errorMsg.append(Translator.toLocale("dob.wrong") + "\r\n");
                    }
                } catch (ParseException e) {
                    errorMsg.append(Translator.toLocale("valid.dob") + "\r\n");
                    log.error("Error parsing date: " + dobCell.getStringCellValue(), e);
                }
            }
        } else {
            //hasDuplicates = true;
            log.warn("Dob is null !");
            errorMsg.append(Translator.toLocale("dob.wrong") + "\r\n");
        }

        Cell roleCell = row.getCell(5);
        if (roleCell != null) {
            if (roleCell.getCellType() == CellType.STRING) {
                String role = roleCell.getStringCellValue();

                log.info("Role is: " + role.toString());
                if (getRoleByRoleName(role) == null) {
                    errorMsg.append(Translator.toLocale("role.wrong") + "\r\n");
                }
            } else
                System.out.println(roleCell.getCellType().name());
        } else {
            log.warn("Role cell is null");
        }
        return errorMsg.toString();
    }


    private void exportExcelWithDuplicates(HttpServletResponse response, Workbook workbook) throws IOException {
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=duplicatedRowExcel.xlsx");

        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();

        System.out.println("File Excel có các dòng lỗi trùng lặp đã được xuất ra.");
    }

    private void saveExcelData(Sheet sheet) {

        for (Row r : sheet) {
            User user = new User();
            InformationOfUser informationOfUser = new InformationOfUser();
            if (r.getRowNum() == 0) {
                continue;
            }

            Cell usernameCell = r.getCell(0);
            Cell emailCell = r.getCell(3);
            if ((usernameCell == null || usernameCell.getCellType() == CellType.BLANK) &&
                    (emailCell == null || emailCell.getCellType() == CellType.BLANK)) {
                break;
            }

            if (usernameCell != null && usernameCell.getCellType() == CellType.STRING) {
                String username = usernameCell.getStringCellValue();
                user.setUsername(username);
            }

            Cell cell = r.getCell(1);
            if (cell == null || cell.getCellType() == CellType.BLANK) {
                log.warn("Password is null");

                user.setPassword(passwordEncoder.encode(defaultPassword));
            } else {
                String password = "";
                log.info("Password is not null");
                if (cell.getCellType() == CellType.STRING) {
                    password = cell.getStringCellValue();
                } else if (cell.getCellType() == CellType.NUMERIC) {
                    password = String.valueOf((long) cell.getNumericCellValue());
                }
                user.setPassword(passwordEncoder.encode(password));

            }

            Cell phoneNumberCell = r.getCell(2);
            if (phoneNumberCell != null) {
                String phoneNumber = phoneNumberCell.getStringCellValue();
                log.info("Phone number: " + phoneNumber);
                informationOfUser.setPhoneNumber(phoneNumber);
            }

            // Email

            if (emailCell != null) {
                String email = emailCell.getStringCellValue();
                log.info("Email: " + email);
                informationOfUser.setEmail(email);
            }

            // Date of Birth
            Cell dobCell = r.getCell(4);
            if (dobCell != null) {
                Date dobDate = dobCell.getDateCellValue();
                log.info(String.valueOf(dobDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()));
                informationOfUser.setDob(dobDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            }
            user.setStatus("ACTIVE");

            Cell roleCell = r.getCell(5);
            if (roleCell != null) {
                String roleName = roleCell.getStringCellValue();
                user.setRole(getRoleByRoleName(roleName));
            }

            user.setInformationOfUser(informationOfUser);
            informationOfUser.setUser(user);
            userRepository.save(user);
        }
    }

    private boolean checkUsername(String username) {
        if (username.length() >= 6 && username.length() <= 30 && username.matches("^[a-zA-Z0-9]+$")) {
            return true;
        }
        return false;
    }

    private boolean checkPassword(String pa) {
        if (pa.length() >= 6 && pa.length() <= 20) {
            return true;
        }
        return false;
    }

    private boolean checkPhoneNumber(String pa)  {
        if (pa.matches("^(?:\\+84|0)(?:[1-9]\\d{8})$")) {
            return true;
        }
        return false;
    }

    private boolean checkEmail(String pa) throws Exception {
        if (pa.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            return true;
        }
        return false;
    }

    private boolean validateDateOfBirth(Date dateOfBirth) {
        try {
            LocalDate localDate = dateOfBirth.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String formattedDate = localDate.format(formatter);

            LocalDate.parse(formattedDate, formatter);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Role getRoleByRoleName(String roleName) {
        if (ROLE_ADMIN.equalsIgnoreCase(roleName) ||
                ROLE_USER.equalsIgnoreCase(roleName) ||
                ROLE_LIBRARIAN.equalsIgnoreCase(roleName) ||
                ROLE_MANAGER.equalsIgnoreCase(roleName)) {
            //Role role = roleRepository.getRolesByRoleName(roleName);
            Role role = roleRepository.findByRoleName(roleName);
            return role;
        }
        return null;
    }
}
