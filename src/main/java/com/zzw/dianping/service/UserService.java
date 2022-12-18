package com.zzw.dianping.service;

import com.zzw.dianping.model.userModel;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    userModel getUser(Integer id);
}
