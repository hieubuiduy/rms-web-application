package com.example.springbootmvcsecurity.service;

import com.example.springbootmvcsecurity.dao.AdminDAO;
import com.example.springbootmvcsecurity.dao.MemberDAO;
import com.example.springbootmvcsecurity.entity.Admin;
import com.example.springbootmvcsecurity.entity.Member;
import com.example.springbootmvcsecurity.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AuthUserDetailsService implements UserDetailsService {


    @Autowired
    private AdminDAO adminDAO;

    @Autowired
    private MemberDAO memberDAO;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        String userName, password;
        Admin admin = adminDAO.findByUserName(s);
        Member member = memberDAO.findByUsername(s);

        if (admin == null && member == null) {
            throw new UsernameNotFoundException(s + " was not found in the database");
        }

        List<GrantedAuthority> grantList= new ArrayList<>();
        if (admin != null) {
            userName = admin.getUserName();
            password = admin.getPassword();
            grantList.add(new SimpleGrantedAuthority(Constants.ADMIN_ROLE));
        }else {
            userName = member.getUserName();
            password = member.getPassword();
            grantList.add(new SimpleGrantedAuthority(Constants.MEMBER_ROLE));
        }
        return new User(userName, password, grantList);
    }
}
