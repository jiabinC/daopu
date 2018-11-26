package com.slabs.api.DAO


import com.slabs.api.entity.DealerBasicInfo
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Select
import org.springframework.stereotype.Component


@Mapper
@Component
interface UserDAO {

    @Select("select * from users ")
    fun getAllUsers():List<DealerBasicInfo>
}