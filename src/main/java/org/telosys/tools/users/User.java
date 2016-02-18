/**
 *  Copyright (C) 2008-2015  Telosys project org. ( http://www.telosys.org/ )
 *
 *  Licensed under the GNU LESSER GENERAL PUBLIC LICENSE, Version 3.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *          http://www.gnu.org/licenses/lgpl.html
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.telosys.tools.users;

import java.io.Serializable;
import java.util.Date;

public class User implements Serializable, Comparable<User> {
	
	private static final long serialVersionUID = 12375932534108120L;

	@Override
	public boolean equals(Object obj) {
		if(obj == null) {
			return false;
		}
		if(!(obj instanceof User)) {
			return false;
		}
		User user2 = (User) obj;
		if (user2.getLogin() == null) {
			return this.getLogin() == null;
		}
		if(this.getLogin() == null) {
			return false;
		}
		return this.getLogin().equals(user2.getLogin());
	}
	
	@Override
	public int compareTo(User user2) {
		if(user2 == null) {
			return 1;
		}
		if (this.getLogin() == null) {
			return (user2.getLogin() == null) ? 0 : -1;
		}
		if(user2.getLogin() == null) {
			return 1;
		}
		return this.getLogin().compareTo(user2.getLogin());
	}
	
	private String login;
	private String mail;
	private String encryptedPassword;
	private String firstName;
	private String lastName;
	private String avatar;
	private Date   lastConnection;
	
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	
	public String getMail() {
		return mail;
	}
	public void setMail(String mail) {
		this.mail = mail;
	}
	
	public String getEncryptedPassword() {
		return encryptedPassword;
	}
	public void setEncryptedPassword(String encryptedPassword) {
		this.encryptedPassword = encryptedPassword;
	}

	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstname) {
		this.firstName = firstname;
	}
	
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastname) {
		this.lastName = lastname;
	}
	
	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	
	public Date getLastConnection() {
		return lastConnection;
	}
	public void setLastConnection(Date lastConnection) {
		this.lastConnection = lastConnection;
	}

	@Override
	public String toString() {
		return "User [login=" + login + ", mail=" + mail
				+ ", passwordEncrypted=" + encryptedPassword + ", firstname="
				+ firstName + ", lastname=" + lastName + ", avatar=" + avatar
				+ ", dateLastConnection=" + lastConnection + "]";
	}
	
}