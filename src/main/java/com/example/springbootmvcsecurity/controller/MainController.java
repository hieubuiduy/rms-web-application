package com.example.springbootmvcsecurity.controller;

import com.example.springbootmvcsecurity.dao.AdminDAO;
import com.example.springbootmvcsecurity.dao.MemberDAO;
import com.example.springbootmvcsecurity.entity.Member;
import com.example.springbootmvcsecurity.formbean.AppUserForm;
import com.example.springbootmvcsecurity.model.Country;
import com.example.springbootmvcsecurity.validator.RegisterMemberValidator;
import com.example.springbootmvcsecurity.web.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.thymeleaf.util.StringUtils;

import java.security.Principal;
import java.util.List;

@Controller
public class MainController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainController.class);
    @Autowired
    private MemberDAO memberDAO;
    @Autowired
    private AdminDAO adminDAO;
    @Autowired
    private RegisterMemberValidator appUserValidator;
    @Autowired
    private List<Country> countries;

    @InitBinder
    protected void initBinder(WebDataBinder dataBinder) {
        Object target = dataBinder.getTarget();
        if (target == null) {
            return;
        }
        LOGGER.info("Target=" + target);

        if (target.getClass() == AppUserForm.class) {
            dataBinder.setValidator(appUserValidator);
        }
        // ...
    }

    @RequestMapping("/")
    public String viewHome(Model model) {
        return "welcomePage";
    }

    @RequestMapping("/members")
    public String viewMembers(Model model) {
        List<Member> list = memberDAO.getMembers();
        model.addAttribute("members", list);
        model.addAttribute("title", "Member List");
        return "membersPage";
    }

    @RequestMapping("/registerSuccessful")
    public String viewRegisterSuccessful(Model model) {
        return "registerSuccessfulPage";
    }

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String viewRegister(Model model) {
        AppUserForm form = new AppUserForm();
        form.setType(AppUserForm.FormType.REGISTER);
        model.addAttribute("appUserForm", form);
        model.addAttribute("countries", countries);
        model.addAttribute("title", "Register page");
        return "registerPage";
    }

    // Phương thức này được gọi để lưu thông tin đăng ký.
    // @Validated: Để đảm bảo rằng Form này
    // đã được Validate trước khi phương thức này được gọi.
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String saveRegister(Model model, //
                               @ModelAttribute("appUserForm") @Validated AppUserForm appUserForm, //
                               BindingResult result, //
                               final RedirectAttributes redirectAttributes) {

        // Validate result
        if (result.hasErrors()) {
            model.addAttribute("countries", countries);
            return "registerPage";
        }
        Member newUser;
        try {
            newUser = memberDAO.createMember(appUserForm);
        }
        // Other error!!
        catch (Exception e) {
            model.addAttribute("countries", countries);
            model.addAttribute("errorMessage", "Error: " + e.getMessage());
            return "registerPage";
        }
        redirectAttributes.addFlashAttribute("flashUser", newUser);
        return "redirect:/registerSuccessful";
    }

    @RequestMapping(value = "/admin/delete/{username}")
    public String deleteMember(Model model, Principal principal, @PathVariable String username) {
        memberDAO.deleteMemberByName(username);
        List<Member> list = memberDAO.getMembers();
        model.addAttribute("members", list);
        model.addAttribute("title", "Member List");
        return "membersPage";
    }

    @RequestMapping(value = "/admin/edit", method = RequestMethod.GET)
    public String viewEditMember(final Model model, Principal principal, @RequestParam("member") String username) {
        if (StringUtils.isEmpty(username)) {
            throw new ResourceNotFoundException();
        }
        Member member = memberDAO.findByUsername(username);
        if (member == null) {
            throw new ResourceNotFoundException();
        }
        AppUserForm currentMember = new AppUserForm();
        currentMember.setType(AppUserForm.FormType.MODIFY);
        currentMember.setUserName(member.getUserName());
        currentMember.setFirstName(member.getFirstName());
        currentMember.setLastName(member.getLastName());
        currentMember.setEmail(member.getEmail());
        currentMember.setCountryCode(member.getCountry());
        currentMember.setGender(member.getGender() ? "M" : "F");

        model.addAttribute("appUserForm", currentMember);
        model.addAttribute("countries", countries);
        return "editMemberPage";
    }

    @RequestMapping(value = "/admin/edit", method = RequestMethod.POST)
    public String saveEditMember(Model model,
                                 @ModelAttribute("appUserForm") @Validated AppUserForm appUserForm, //
                                 BindingResult result,
                                 Principal principal,
                                 final RedirectAttributes redirectAttributes) {
        // Validate result
        if (result.hasErrors()) {
            model.addAttribute("countries", countries);
            return "editMemberPage";
        }

        memberDAO.updateMemberByName(appUserForm);
        return "redirect:/members";
    }

    @RequestMapping(value = "/admin/register", method = RequestMethod.GET)
    public String viewAddAdmin(Model model) {
        AppUserForm form = new AppUserForm();
        form.setType(AppUserForm.FormType.REGISTER);
        form.setEmail("example@example.com");
        form.setFirstName("example");
        form.setLastName("example");
        form.setGender("example");
        form.setCountryCode("example");
        model.addAttribute("appUserForm", form);
        return "registerAdminPage";
    }

    @RequestMapping(value = "/admin/register", method = RequestMethod.POST)
    public String saveAddAdmin(Model model,
                                 @ModelAttribute("appUserForm") @Validated AppUserForm appUserForm, //
                                 BindingResult result,
                                 Principal principal,
                                 final RedirectAttributes redirectAttributes) {
        // Validate result
        if (result.hasErrors()) {
            return "registerAdminPage";
        }

        adminDAO.addAdmin(appUserForm);
        return "redirect:/";
    }

    @GetMapping("/user")
    public String userIndex() {
        return "user/index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "/error/access-denied";
    }

    @ExceptionHandler({ResourceAccessException.class, MissingServletRequestParameterException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleResourceNotFoundException(final Model model) {

        return "/error/notFound";
    }

}
