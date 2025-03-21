package com.yeoboya.lunch.config.security.validation;

import javax.validation.groups.Default;

public class ValidationGroups {


    public interface PatternCheckGroup {
    }


    public interface EmailCheckGroup {
    }


    public interface KnowOldPassword extends Default {
    }


    public interface UnKnowOldPassword extends Default {
    }


    public interface NormalSignUpGroup extends Default {
    }

    public interface SocialSignUpGroup extends Default {
    }
}