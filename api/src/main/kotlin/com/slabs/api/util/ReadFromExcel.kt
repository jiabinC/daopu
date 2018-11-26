package com.slabs.api.util

import com.slabs.api.entity.DealerBasicInfo
import org.apache.poi.ss.formula.functions.T
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.springframework.stereotype.Component
import java.io.File
import java.io.FileInputStream
import kotlin.reflect.KClass


@Component
object ReadFromExcel{

    fun  getListFromExcel(filePath:String,sheet:String,classzz:KClass<T>) : List<T>{
        val DTOList = mutableListOf<T>()
        val excelFile =FileInputStream(File(filePath))
        val workbook = XSSFWorkbook(excelFile)
        val sheet = workbook.getSheet(sheet)
        val user = DealerBasicInfo()
//        var DTO = when(type) {
//            User::class -> User()
//
//            else -> throw Exception("error")
//
//        }
        classzz.objectInstance

        val list = mutableListOf<Any>()
        val rows = sheet.iterator()
        rows.next()
        rows.remove()
        while (rows.hasNext()){

            val currentRow = rows.next()
            val cellsInRow = currentRow.iterator()
            while (cellsInRow.hasNext()){
                val currentCell = cellsInRow.next()

//                when(currentCell.cellTypeEnum) {
//
//                    is CellDateFormatter -> print(currentCell.dateCellValue)
//
//                }
//                user.age = currentCell.stringCellValue


               print (currentCell)
            }


        }
        return listOf()

    }





}