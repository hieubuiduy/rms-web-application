package com.example.springbootmvcsecurity.validator;

import com.example.springbootmvcsecurity.controller.MainController;
import com.example.springbootmvcsecurity.dao.AdminDAO;
import com.example.springbootmvcsecurity.dao.MemberDAO;
import com.example.springbootmvcsecurity.entity.Admin;
import com.example.springbootmvcsecurity.entity.Member;
import com.example.springbootmvcsecurity.formbean.AppUserForm;
import org.apache.commons.validator.routines.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
public class RegisterMemberValidator implements Validator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainController.class);

    // common-validator library.
    private EmailValidator emailValidator = EmailValidator.getInstance();

    @Autowired
    private MemberDAO memberDAO;

    @Autowired
    private AdminDAO adminDAO;

    // Các lớp được hỗ trợ bởi Validator này.
    @Override
    public boolean supports(Class<?> clazz) {
        return clazz == AppUserForm.class;
    }

    @Override
    public void validate(Object target, Errors errors) {
        AppUserForm appUserForm = (AppUserForm) target;

        // Kiểm tra các field của AppUserForm.
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "userName", "NotEmpty.appUserForm.userName");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstName", "NotEmpty.appUserForm.firstName");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastName", "NotEmpty.appUserForm.lastName");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "NotEmpty.appUserForm.email");
        if (appUserForm.getType() == AppUserForm.FormType.REGISTER) {
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "NotEmpty.appUserForm.password");
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "confirmPassword", "NotEmpty.appUserForm.confirmPassword");
        }
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "gender", "NotEmpty.appUserForm.gender");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "countryCode", "NotEmpty.appUserForm.countryCode");

        String emailForm = appUserForm.getEmail();
        String userNameForm = appUserForm.getUserName();
        AppUserForm.FormType typeForm = appUserForm.getType();
        //Validate Email
        LOGGER.info("Start validate Email --Type: " + appUserForm.getType());
        if (!this.emailValidator.isValid(emailForm)) {
            // Email invalid
            errors.rejectValue("email", "Pattern.appUserForm.email");
        } else if (typeForm == AppUserForm.FormType.REGISTER && memberDAO.isEmailExist(emailForm)) {
            // Email has been used by another account (Register)
            errors.rejectValue("email", "Duplicate.appUserForm.email");
        } else if (typeForm == AppUserForm.FormType.MODIFY) {
            // Email has been used by another account (Edit)
            LOGGER.info("Check email is used by another account --Edit");
            String userNameByEmail = memberDAO.getUsernameByEmail(emailForm);
            if (userNameByEmail != null && !userNameByEmail.equals(userNameForm)) {
                errors.rejectValue("email", "Duplicate.appUserForm.email");
            }
        }

        //validate user
        if (typeForm == AppUserForm.FormType.REGISTER) {
            if (!errors.hasFieldErrors("userName")) {
                Member dbUser = memberDAO.findByUsername(userNameForm);
                Admin dbUser2 = adminDAO.findByUserName(userNameForm);
                if (dbUser != null || dbUser2 != null) {
                    // Tên tài khoản đã bị sử dụng bởi người khác.
                    errors.rejectValue("userName", "Duplicate.appUserForm.userName");
                }
            }

            if (!errors.hasErrors()) {
                if (!appUserForm.getConfirmPassword().equals(appUserForm.getPassword())) {
                    errors.rejectValue("confirmPassword", "Match.appUserForm.confirmPassword");
                }
            }
        }
    }

}
