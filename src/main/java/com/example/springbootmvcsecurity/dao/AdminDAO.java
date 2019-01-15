package com.example.springbootmvcsecurity.dao;

import com.example.springbootmvcsecurity.entity.Admin;
import com.example.springbootmvcsecurity.formbean.AppUserForm;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class AdminDAO {

    @Autowired
    private SessionFactory sessionFactory;

    // Config in WebSecurityConfig
    @Autowired
    private PasswordEncoder passwordEncoder;

    public AdminDAO() {
    }

    public Admin findByUserName(String name){
        Session session = sessionFactory.getCurrentSession();
        return session.get(Admin.class, name);
    }

    public Admin addAdmin(AppUserForm memberForm){
        Session session = sessionFactory.getCurrentSession();
        Admin admin = new Admin();
        admin.setUserName(memberForm.getUserName());
        admin.setPassword(passwordEncoder.encode(memberForm.getPassword()));
        session.persist(admin);
        return admin;
    }

}
