package com.techJob.domain.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.techJob.domain.enums.Gender;
import com.techJob.domain.enums.Roles;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    @Column(unique = true)
    private String username;
    @Column(unique = true)
    private String email;
    private String password;
    @Column(nullable = false)
    private Boolean emailVerified=false;
    
    @OneToOne(mappedBy = "user",cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.LAZY)
    private Address address;
    @Column(unique = true)
    private String phoneNumber;
    @Enumerated(EnumType.STRING)
    private Gender gender;
    private LocalDate dateOfBirth;
    
    private String profilImageUrl;

    @Column(nullable = false, unique = true, updatable = false)
    private String publicID;
    @Column( updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private ArtisanProfile artisanProfile;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Roles role;
    @Column(nullable = false)
    private Boolean deleted = false;
    private LocalDateTime deletedAt;
    private Integer tokenVersion = 0;
    // ========= UserDetails =========

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (role == null) return Collections.emptyList();
        return Set.of(new SimpleGrantedAuthority(
        		role.toString().startsWith("ROLE_") ?
        				role.toString() : "ROLE_" + role.toString()));
    }

    @Override
    public String getUsername() {
        return username; // ✅ مهم جدًا
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return !deleted;
    }
    // getters & setters عادية

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	

	

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	

	
	public String getPublicID() {
		return publicID;
	}
	public void setPublicID(String publicID) {
		this.publicID = publicID;
	}

	public ArtisanProfile getArtisanProfile() {
		return artisanProfile;
	}

	public void setArtisanProfile(ArtisanProfile artisanProfile) {
		this.artisanProfile = artisanProfile;
	}

    public Roles getRole() {
        return role;
    }

    public void setRole(Roles role) {
        this.role = role;
    }

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public String getProfilImageUrl() {
		return profilImageUrl;
	}

	public void setProfilImageUrl(String profilImageUrl) {
		this.profilImageUrl = profilImageUrl;
	}

	public Boolean getEmailVerified() {
		return emailVerified;
	}

	public void setEmailVerified(Boolean emailVerified) {
		this.emailVerified = emailVerified;
	}

	public Address getAddress() {
        return address;
    }
    public void setAddress(Address address) {
        this.address = address;
    }

	public LocalDate getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(LocalDate dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public Boolean getDeleted() {
		return deleted;
	}

	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}

	public LocalDateTime getDeletedAt() {
		return deletedAt;
	}

	public void setDeletedAt(LocalDateTime deletedAt) {
		this.deletedAt = deletedAt;
	}

	public Integer getTokenVersion() {
	    return tokenVersion == null ? 0 : tokenVersion;
	}

	public void setTokenVersion(Integer tokenVersion) {
		this.tokenVersion = tokenVersion;
	}
	
	
	
}