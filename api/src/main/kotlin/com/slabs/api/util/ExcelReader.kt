package com.slabs.api.util

import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.util.ArrayList
import java.util.HashMap
import org.apache.poi.ss.usermodel.*
import java.text.SimpleDateFormat

/**
 *
 *
 * @author      abin
 * @date        2018/11/23 8:56
 * @version     1.0
 */
class ExcelReader(private val filePath: String, private val sheetName: String) {
    private var workBook: Workbook? = null
    private var sheet: Sheet? = null
    private var columnHeaderList: MutableList<String>? = null
    private var listData: MutableList<List<String>>? = null
    private var mapData: MutableList<Map<String, String>>? = null
    private var flag: Boolean = false


    init {
        this.flag = false
        this.load()
    }


    private fun load() {
        var inStream: FileInputStream? = null
        try {
            inStream = FileInputStream(File(filePath))
            workBook = WorkbookFactory.create(inStream)
            sheet = workBook!!.getSheet(sheetName)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                inStream?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
    }


    private fun getCellValue(cell: Cell?): String {
        var cellValue = ""
        val formatter = DataFormatter()
        val formatter1 =SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        if (cell != null) {
            when (cell.cellTypeEnum) {
                CellType.NUMERIC -> if (DateUtil.isCellDateFormatted(cell)) {
                    val cellData = cell.dateCellValue
                    cellValue = formatter1.format(cellData)
                } else {
                    val value = cell.numericCellValue
                    val intValue = value.toInt()
                    cellValue = if (value - intValue == 0.0) intValue.toString() else value.toString()
                }
                CellType.STRING -> cellValue = cell.stringCellValue
                CellType.BOOLEAN -> cellValue = cell.booleanCellValue.toString()
                CellType.FORMULA -> cellValue = cell.cellFormula.toString()
                CellType.BLANK -> cellValue = ""
                CellType.ERROR -> cellValue = ""
                else -> cellValue = cell.toString().trim { it <= ' ' }
            }
        }
        return cellValue.trim { it <= ' ' }
    }


    private fun getSheetData() {
        listData = ArrayList()
        mapData = ArrayList()
        columnHeaderList = ArrayList()
        val numOfRows = sheet!!.lastRowNum + 1
        for (i in 0 until numOfRows) {
            val row = sheet!!.getRow(i)
            val map = HashMap<String, String>()
            val list = ArrayList<String>()
            if (row != null) {
                for (j in 0 until row.lastCellNum) {
                    val cell = row.getCell(j)
                    if (i == 0) {
                        columnHeaderList!!.add(getCellValue(cell))
                    } else {
                        map[columnHeaderList!![j]] = this.getCellValue(cell)
                    }
                    list.add(this.getCellValue(cell))
                }
            }
            if (i > 0) {
                mapData!!.add(map)
            }
            listData!!.add(list)
        }
        flag = true
    }


    private fun getCellData(row: Int, col: Int): String? {
        if (row <= 0 || col <= 0) {
            return null
        }
        if (!flag) {
            this.getSheetData()
        }
        return if (listData!!.size >= row && listData!![row - 1].size >= col) {
            listData!![row - 1][col - 1]
        } else {
            null
        }
    }


    fun getCellData(row: Int, headerName: String): String? {
        if (row <= 0) {
            return null
        }
        if (!flag) {
            this.getSheetData()
        }
        return if (mapData!!.size >= row && mapData!![row - 1].containsKey(headerName)) {
            mapData!![row - 1][headerName]
        } else {
            null
        }
    }


    /**
     * 获取标题
     *
     * @param eh
     * @param maxX
     * @return
     */
    fun getTitleList(eh: ExcelReader, maxX: Int): List<String?> {
        val result = ArrayList<String?>()
        for (i in 1..maxX) {
            result.add(eh.getCellData(1, i))
        }
        return result
    }


    /**
     * 获取单行对象
     *
     * @param
     * @param eh
     *
     * @param titles
     * @return
     */
    @Throws(Exception::class)
    fun getObject(className: String, eh: ExcelReader, y: Int, titles: List<String?>): Any {
        val bean = Class.forName(className).newInstance()
        val length = titles.size
        for (x in 0 until length) {
            try {
                val field = bean.javaClass.getDeclaredField(titles[x])
                field.isAccessible = true
                when (field.type) {
                    java.lang.Double::class.java -> field.set(bean, eh.getCellData(y,x + 1)?.toDouble())
                    String::class.java -> field.set(bean, eh.getCellData(y, x + 1))
                    else -> println(field.type )
                }
              //  field.set(bean, eh.getCellData(y, x + 1))
            } catch (e: Exception) {
                println("没有对应的方法：$e")
            }

        }
        return bean
    }


    /**
     * 获取Excel数据列表
     *
     * @param
     * @param eh
     * @param x
     * 每行有多少列数据
     * @param y
     * 整个sheet有多少行数据
     * @param titles
     * @return
     */
    fun getDataList(clazz: Class<*>, eh: ExcelReader, x: Int, y: Int, titles: List<String?>): List<Any> {
        val result = ArrayList<Any>()
        val className = clazz.name
        try {
            for (i in 2..y) {
                val `object` = eh.getObject(className, eh, i, titles)
                result.add(`object`)
            }
        } catch (e: Exception) {
            println(e)
        }

        return result
    }

//    companion object {
//
//
//        @JvmStatic
//        fun main(args: Array<String>) {
//            try {
//                val eh = ExcelReader("C:\\Users\\Neo\\Desktop\\POI.xlsx", "Sheet1")
//                val titles = eh.getTitleList(eh, 7)
//                val userList = eh.getDataList(User::class.java, eh, 7, 4, titles)
//                for (`object` in userList) {
//                    println(`object`)
//                }
//            } catch (e: Exception) {
//                println(e)
//            }
//
//        }
//    }
}

