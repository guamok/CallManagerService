package es.fermax.callmanagerservice.util;

import es.fermax.callmanagerservice.exception.UserIdForAdminMandatoryException;
import es.fermax.callmanagerservice.exception.UserNotExistException;
import es.fermax.fermaxsecurity.UserIDEncryptorService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;

public class UserUtils {

	private UserUtils() {
	}

	/**
	 * Get the user identifier
	 * 
	 * @param authentication
	 * @param userIDEncryptorService
	 * @return userId
	 */
	public static Integer getUserId(Authentication authentication, UserIDEncryptorService userIDEncryptorService) {
		Integer userId;
		try {
			userId = Integer.valueOf(userIDEncryptorService.decrypt(authentication.getName()));
		} catch (Exception e) {
			throw new UserNotExistException();
		}
		return userId;
	}

	/**
	 * Checks if a user is admin, manager or management
	 *
	 * @param authentication authentication like a input for seeing its role
	 * @return boolean
	 */
	public static boolean isRoleAuthorized(Authentication authentication) {
		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
		return isRoleAdmin(authorities) || isRoleManager(authorities) || isRoleManagement(authorities);
	}

	/**
	 * Recovers the user
	 *
	 * @param authentication
	 * @param userId
	 * @param userIDEncryptorService
	 * @return userId
	 * @throws Exception
	 */
	public static Integer retrieveUserId(Authentication authentication, Integer userId,
			UserIDEncryptorService userIDEncryptorService) {
		if (UserUtils.isRoleAuthorized(authentication)) {
			if (userId == null) {
				throw new UserIdForAdminMandatoryException();
			}
		} else {
			userId = getUserId(authentication, userIDEncryptorService);
		}
		return userId;
	}

	/**
	 * Checks if a user is Management
	 *
	 * @param authorities
	 * @return boolean
	 */
	private static boolean isRoleManagement(Collection<? extends GrantedAuthority> authorities) {
		return authorities.contains((new SimpleGrantedAuthority("ROLE_MANAGEMENT")));
	}

	/**
	 * Checks if a user is Manager
	 *
	 * @param authorities
	 * @return boolean
	 */
	private static boolean isRoleManager(Collection<? extends GrantedAuthority> authorities) {
		return authorities.contains((new SimpleGrantedAuthority("ROLE_MANAGER")));
	}

	/**
	 * Checks if a user is Admin
	 *
	 * @param authorities
	 * @return boolean
	 */
	private static boolean isRoleAdmin(Collection<? extends GrantedAuthority> authorities) {
		return authorities.contains((new SimpleGrantedAuthority("ROLE_ADMIN")));
	}
}
