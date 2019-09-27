package com.dlut.mnist.scriptrecognizer.DAO;

import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvConstraintViolationException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class CsvDataConverter extends AbstractBeanField {
    @Override
    protected Object convert(String value) throws CsvDataTypeMismatchException, CsvConstraintViolationException {

        HashMap<String,Integer> map = null;


        //TODO:NOT COMPLETE!!!


        List<String> l = null;
        if (!StringUtils.isEmpty(value)) {
            l = new ArrayList<>(Arrays.asList(value.split("\\s+")));
        }
        return l;
    }
}
