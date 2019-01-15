package com.example.springbootmvcsecurity.dao;

import com.example.springbootmvcsecurity.entity.Member;
import com.example.springbootmvcsecurity.formbean.AppUserForm;
import com.example.springbootmvcsecurity.model.Gender;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.*;
import java.util.List;

@Repository
@Transactional
public class MemberDAO {

    @Autowired
    private SessionFactory sessionFactory;

    // Config in WebSecurityConfig
    @Autowired
    private PasswordEncoder passwordEncoder;

    public MemberDAO(){

    }

    public Member findByUsername(String userName){
        Session session = sessionFactory.getCurrentSession();
        return session.get(Member.class, userName);
    }

    public List<Member> findByEmail(String email){
        Session session = sessionFactory.getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Member> criteria = builder.createQuery(Member.class);
        Root<Member> root = criteria.from(Member.class);
        criteria.select(root).where(builder.equal(root.get("email"), email));
        Query<Member> query = session.createQuery(criteria);
        return query.getResultList();
    }

    public boolean isEmailExist(String email){
        List<Member> members = findByEmail(email);
        return (members != null && members.size() != 0);
    }

    @SuppressWarnings("unchecked")
    public List<Member> getMembers(){
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery("from " + Member.class.getName()).list();
    }

    public Member createMember(AppUserForm memberForm){
        Session session = sessionFactory.getCurrentSession();
        Member member = new Member();
        member.setUserName(memberForm.getUserName());
        member.setEmail(memberForm.getEmail());
        member.setPassword(passwordEncoder.encode(memberForm.getPassword()));
        member.setFirstName(memberForm.getFirstName());
        member.setLastName(memberForm.getLastName());
        member.setGender(Gender.MALE.equals(memberForm.getGender()));
        member.setCountry(memberForm.getCountryCode());
        session.persist(member);
        return member;
    }

    public void deleteMemberByName(String userName){
        Session session = sessionFactory.getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaDelete<Member> criteria = builder.createCriteriaDelete(Member.class);
        Root<Member> root = criteria.from(Member.class);
        criteria.where(builder.equal(root.get("userName"), userName));
        session.createQuery(criteria).executeUpdate();
    }

    public String getUsernameByEmail(String email){
        List<Member> members = findByEmail(email);
        if (members == null || members.size() > 1){
            return null;
        }
        return members.get(0).getUserName();
    }

    public void updateMemberByName(AppUserForm member){
        Session session = sessionFactory.getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaUpdate<Member> criteriaUpdate = builder.createCriteriaUpdate(Member.class);
        Root<Member> root = criteriaUpdate.from(Member.class);
        criteriaUpdate.set("email", member.getEmail());
        criteriaUpdate.set("firstName", member.getFirstName());
        criteriaUpdate.set("lastName", member.getLastName());
        criteriaUpdate.set("gender", Gender.MALE.equals(member.getGender()));
        criteriaUpdate.set("country", member.getCountryCode());
        criteriaUpdate.where(builder.equal(root.get("userName"), member.getUserName()));
        session.createQuery(criteriaUpdate).executeUpdate();
    }

}
