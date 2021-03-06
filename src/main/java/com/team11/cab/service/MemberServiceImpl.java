package com.team11.cab.service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team11.cab.model.Authority;
import com.team11.cab.model.Member;
import com.team11.cab.repository.AuthorityRepository;
import com.team11.cab.repository.MemberRepository;

@Service
public class MemberServiceImpl implements MemberService, UserDetailsService {

	@Resource
	private MemberRepository memberRepository;

	@Resource
	private AuthorityRepository authorityRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	@Transactional
	public ArrayList<Member> findAllMember() {

		ArrayList<Member> members = (ArrayList<Member>) memberRepository.findAll();
		return members;
	}

	@Override
	@Transactional
	public void editMember(Member m) {
		Member memberFromDb = findMemberByUsername(m.getUsername());

		memberFromDb.setEmail(m.getEmail());
		memberFromDb.setFirstName(m.getFirstName());
		memberFromDb.setLastName(m.getLastName());
		memberFromDb.setDob(m.getDob());
		memberFromDb.setAddress(m.getAddress());
		memberFromDb.setPhone(m.getPhone());
		memberFromDb.setEnabled(m.isEnabled());
		if (m.isChangeAdmin())
			addAdminRights(memberFromDb);
		else
			removeAdminRights(memberFromDb);

		memberRepository.saveAndFlush(memberFromDb);
	}

	// @Override
	// @Transactional
	// public void deleteMember(Member m) {
	// memberRepository.delete(m);
	// }

	public Member findMemberByUsername(String username) {
		return memberRepository.findByUsername(username);
	}

	@Override
	public void saveMember(Member member) {
		member.setPassword(passwordEncoder.encode(member.getPassword()));
		member.setEnabled(true);

		Authority authority = new Authority();
		authority.setMember(member);
		authority.setAuthority("ROLE_USER");

		member.setAuthorities(new HashSet<Authority>(Arrays.asList(authority)));

		memberRepository.save(member);
	}

	@Override
	@Transactional
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Member member = memberRepository.findByUsername(username);
		List<GrantedAuthority> authorities = getUserAuthority(member.getAuthorities());
		return buildUserForAuthentication(member, authorities);
	}

	private List<GrantedAuthority> getUserAuthority(Set<Authority> userAuthorities) {
		Set<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();
		for (Authority authority : userAuthorities) {
			authorities.add(new SimpleGrantedAuthority(authority.getAuthority()));
		}

		List<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>(authorities);
		return grantedAuthorities;
	}

	private UserDetails buildUserForAuthentication(Member member, List<GrantedAuthority> authorities) {
		return new org.springframework.security.core.userdetails.User(member.getEmail(), member.getPassword(),
				member.isEnabled(), true, true, true, authorities);
	}

	@Override
	@Transactional
	public void updateMember(Member m) {
		Principal principal = SecurityContextHolder.getContext().getAuthentication();
		Member memberFromDb = findMemberByUsername(principal.getName());
		memberFromDb.setEmail(m.getEmail());
		memberFromDb.setFirstName(m.getFirstName());
		memberFromDb.setLastName(m.getLastName());
		memberFromDb.setDob(m.getDob());
		memberFromDb.setAddress(m.getAddress());
		memberFromDb.setPhone(m.getPhone());

		memberRepository.saveAndFlush(memberFromDb);
	}

	@Override
	public Member findMemberById(int id) {
		return memberRepository.findOne(id);
	}

	@Override
	public void removeAdminRights(Member member) {
		member.getAuthorities().removeIf(x -> x.getAuthority().equals("ROLE_ADMIN"));
		System.out.println(member.getAuthorities().size());
		memberRepository.saveAndFlush(member);
	}

	@Override
	public void addAdminRights(Member member) {
		System.out.println("ADDING ADMIN RIGHTS");
		if (member.getAuthorities().stream().filter(x -> x.getAuthority().equals("ROLE_ADMIN")).count() > 0)
			return;
		Authority authority = new Authority();
		authority.setAuthority("ROLE_ADMIN");
		authority.setMember(member);
		member.getAuthorities().add(authority);
		
		memberRepository.saveAndFlush(member);
	}

}
