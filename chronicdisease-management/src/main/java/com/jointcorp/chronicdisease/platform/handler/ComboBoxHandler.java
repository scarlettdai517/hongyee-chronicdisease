package com.jointcorp.chronicdisease.platform.handler;

import com.alibaba.excel.write.handler.SheetWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteWorkbookHolder;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddressList;

import java.util.Map;

/**
 * excel下载模板下拉框赋值
 * @Author zHuH1
 * @Date 2023/5/22 16:02
 **/
public class ComboBoxHandler implements SheetWriteHandler {

    // 下拉框值
    private Map<Integer, String[]> comboBoxMap;
    // 设置有下拉值的行数
    private final static Integer rowSize = 200;

    public ComboBoxHandler(Map<Integer, String[]> comboBoxMap) {
        this.comboBoxMap = comboBoxMap;
    }

    @Override
    public void beforeSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {
    }

    @Override
    public void afterSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {
        Sheet sheet = writeSheetHolder.getSheet();
        DataValidationHelper helper = sheet.getDataValidationHelper();
        comboBoxMap.forEach((celIndex, strings) -> {
            // 区间设置
            CellRangeAddressList cellRangeAddressList = new CellRangeAddressList(1, rowSize, celIndex, celIndex);
            // 下拉内容
            DataValidationConstraint constraint = helper.createExplicitListConstraint(strings);
            DataValidation dataValidation = helper.createValidation(constraint, cellRangeAddressList);
            dataValidation.setErrorStyle(DataValidation.ErrorStyle.STOP);
            dataValidation.setShowErrorBox(true);
            dataValidation.setSuppressDropDownArrow(true);
            dataValidation.createErrorBox("提示", "此值不符合此单元格要求!");

            sheet.addValidationData(dataValidation);
        });
    }
}
