package com.vijay.User_Master.config;

import com.vijay.User_Master.Helper.CommonUtils;
import com.vijay.User_Master.config.security.CustomUserDetails;
import org.springframework.data.domain.AuditorAware;

import java.util.Optional;



public class AuditorConfig implements AuditorAware<Integer> {



    @Override
    public Optional<Integer> getCurrentAuditor() {
        try {
            CustomUserDetails loggedInUser = CommonUtils.getLoggedInUser();
            if (loggedInUser != null) {
                return Optional.of(Math.toIntExact(loggedInUser.getId()));
            } else {
                // Return a default user ID when no logged-in user is available
                return Optional.of(1);
            }
        } catch (Exception e) {
            // Handle any exceptions and return the default user ID
            return Optional.of(1);
        }
    }

    // you are running first time application then this method un-comments this.


/*    @Override
    public Optional<Integer> getCurrentAuditor() {
        CustomUserDetails loggedInUser = CommonUtils.getLoggedInUser();
        return Optional.of(Math.toIntExact(loggedInUser.getId()));
    }*/


}
