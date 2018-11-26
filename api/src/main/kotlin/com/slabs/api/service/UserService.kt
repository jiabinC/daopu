package com.slabs.api.service

import com.slabs.api.DAO.UserDAO
import com.slabs.api.entity.DealerBasicInfo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service


@Service
class UserService {

    @Autowired
    lateinit var userDao:UserDAO


    fun getUsers():List<DealerBasicInfo>{
        return userDao.getAllUsers()
    }

}