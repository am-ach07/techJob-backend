package com.exception.artisan;

import java.util.Set;

import com.domain.enums.Skills;
import com.exception.BusinessException;

public class SkillCannotBeNull extends BusinessException {

	public SkillCannotBeNull() {
		super("Artisan must provide at least one skill");
		// TODO Auto-generated constructor stub
	}

}
