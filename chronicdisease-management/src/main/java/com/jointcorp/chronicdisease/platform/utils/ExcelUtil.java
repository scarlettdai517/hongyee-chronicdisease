package com.jointcorp.chronicdisease.platform.utils;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import com.jointcorp.chronicdisease.platform.handler.ComboBoxHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.poifs.filesystem.FileMagic;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;


/**
 * @Author zHuH1
 * @Date 2023/5/22 10:16
 **/
@Slf4j
public class ExcelUtil {

    /**
     * 导出
     * @param response
     * @param data
     * @param fileName
     * @param sheetName
     * @param clazz
     * @throws Exception
     */
    public static void writeExcel(HttpServletResponse response, List<? extends Object> data, String fileName, String sheetName, Class clazz) throws Exception {
        //表头样式
        WriteCellStyle headWriteCellStyle = new WriteCellStyle();
        //设置表头居中对齐
        headWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
        //内容样式
        WriteCellStyle contentWriteCellStyle = new WriteCellStyle();
        //设置内容靠左对齐
        contentWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.LEFT);
        HorizontalCellStyleStrategy horizontalCellStyleStrategy = new HorizontalCellStyleStrategy(headWriteCellStyle, contentWriteCellStyle);
        EasyExcel.write(getOutputStream(fileName, response), clazz)
                .excelType(ExcelTypeEnum.XLSX)
                .sheet(sheetName)
                .registerWriteHandler(horizontalCellStyleStrategy)
                .doWrite(data);
    }


    /**
     * 下载模板
     * @param response
     * @param data
     * @param fileName
     * @param sheetName
     * @param clazz
     * @throws Exception
     */
    public static void writeExcel(HttpServletResponse response, List<? extends Object> data, String fileName,
                                  String sheetName, Class clazz, Map<Integer, String[]> comboBoxMap) throws Exception {
        //表头样式
        WriteCellStyle headWriteCellStyle = new WriteCellStyle();
        //设置表头居中对齐
        headWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
        //内容样式
        WriteCellStyle contentWriteCellStyle = new WriteCellStyle();
        //设置内容靠左对齐
        contentWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.LEFT);
        HorizontalCellStyleStrategy horizontalCellStyleStrategy = new HorizontalCellStyleStrategy(headWriteCellStyle, contentWriteCellStyle);
        EasyExcel.write(getOutputStream(fileName, response), clazz)
                .excelType(ExcelTypeEnum.XLSX)
                .sheet(sheetName)
                .registerWriteHandler(horizontalCellStyleStrategy)
                .registerWriteHandler(new ComboBoxHandler(comboBoxMap))
                .doWrite(data);
    }

    public static OutputStream getOutputStream(String fileName, HttpServletResponse response) throws Exception {
        fileName = URLEncoder.encode(fileName, "UTF-8");
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf8");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");
        return response.getOutputStream();
    }

    /**
     * 校验上传的文件是否是Excel文件及是否是xls或xlsx格式
     *
     * @param file
     * @return
     * @throws Exception
     */
    public static Boolean checkExcelFile(MultipartFile file) throws Exception {
        Boolean result = false;
        try {
            //校验文件名称
            String fileName = file.getOriginalFilename();
            if (fileName == null) {
                log.error("未获取到文件名");
                throw new Exception("未获取到文件名");
            }
            //校验文件后缀名
            String fileSuffixName = fileName.substring(fileName.lastIndexOf(".") + 1);
            if (fileSuffixName == null) {
                log.error("非法文件");
                throw new Exception("非法文件");
            }
            //校验文件后缀名是否为xls或者xlsx格式
            if (!"xls".equals(fileSuffixName) && !"xlsx".equals(fileSuffixName)) {
                log.error("文件格式错误");
                throw new Exception("文件格式错误，请上传xls或xlsx格式的Excel文件");
            }
            //根据Excel魔数判断该文件是否为Excel文件
            InputStream inputStream = file.getInputStream();
            InputStream fileMagics = FileMagic.prepareToCheckMagic(inputStream);
            FileMagic fileMagic = FileMagic.valueOf(fileMagics);
            //FileMagic.OLE2表示xls格式 FileMagic.OOXML表示xlsx格式
            if (Objects.equals(fileMagic, FileMagic.OLE2) || Objects.equals(fileMagic, FileMagic.OOXML)) {
                result = true;
            } else {
                log.error("文件格式错误");
                throw new Exception("文件格式错误，请上传xls或xlsx格式的Excel文件");
            }
        } catch (Exception e) {
            log.error("请上传xls或xlsx格式的Excel文件", e);
            throw e;
        }
        return result;
    }


//    /*
//     * Excel转换实体类对象
//     */
//    public static List<Student> excelListConvertList(List<StudentExcel> studentExcelList) throws ParseException {
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//        List<Student> list = new ArrayList<>();
//        for (StudentExcel s : studentExcelList) {
//            Student student = Student.builder()
//                    .name(staffDataExcel.getname())
//                    .age(staffDataExcel.age())
//                    .city(staffDataExcel.getCity())
//                    .sex(staffDataExcel.getsex().equals("男")?1:2)
//                    .birthday(sdf.parse(staffDataExcel.getBirthday()))
//                    .build();
//            list.add(student);
//        }
//        return list ;
//    }
//
//    /*
//     *  导出的List集合 转换 Excel
//     */
//    public static List<StudentExportExcel> listConvertexcelList(List<Student> student) throws ParseException {
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//        List<StudentExcel> list= new ArrayList<>();
//        for (Student s : student) {
//            StudentExcel excel = StudentExcel.builder()
//                    .name(staffDataExcel.getname())
//                    .age(staffDataExcel.age())
//                    .city(staffDataExcel.getCity())
//                    .sex(staffDataExcel.getsex().equals("男")?1:2)
//                    .birthday(sdf.parse(staffDataExcel.getBirthday()))
//                    .build();
//                    .build();
//            list.add(excel);
//        }
//        return list;
//    }

}


