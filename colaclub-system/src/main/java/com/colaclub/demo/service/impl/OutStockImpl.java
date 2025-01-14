package com.colaclub.demo.service.impl;

import com.colaclub.demo.mapper.OutStockMapper;
import com.colaclub.demo.service.OutStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
public class OutStockImpl implements OutStockService {

    @Autowired
    private OutStockMapper outStockMapper;

    @Override
    @Transactional
    public String updateStock(Long id, Integer number) {
        try {
            // Prepare parameters in a map
            Map<String, Object> params = new HashMap<>();
            params.put("id", id);
            params.put("number", number);

            // Call Mapper to update stock
            int result = outStockMapper.updateStock(params);

            // Handle success or failure
            if (result > 0) {
                return "购买成功";
            } else {
                return "库存不足，购买失败";
            }
        } catch (Exception e) {
            // Log error and rethrow
            e.printStackTrace();
            throw new RuntimeException("购买失败，发生错误：" + e.getMessage());
        }
    }

}
