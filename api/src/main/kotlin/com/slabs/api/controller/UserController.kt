package com.slabs.api.controller

import com.slabs.api.service.UserService
import com.slabs.api.util.ExcelReader
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController


@RestController
class UserController {

    @Autowired
    lateinit var  userService: UserService

    @GetMapping("/get")
    fun getAllUser():List<com.slabs.api.entity.DealerBasicInfo> {
       return userService.getUsers()

    }
    @GetMapping("/test")
    fun test() :List<Any> {
        //
        // return ReadFromExcel.getListFromExcel<UserDAO>("D:\\1.xlsx","Sheet1",com.slabs.api.entity.User::class)
        val excelReader = ExcelReader("F:\\BankLoanInfo.xlsx","工商银行放款信息")
        val title = excelReader.getTitleList(excelReader,12)
        val userList = excelReader.getDataList(com.slabs.api.entity.BankLoanInfo::class.java,excelReader,12,9999,title)
        return userList

    }
}