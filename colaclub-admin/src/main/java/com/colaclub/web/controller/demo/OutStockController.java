package com.colaclub.web.controller.demo;

import com.colaclub.common.annotation.Anonymous;
import com.colaclub.demo.service.OutStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/outStock")
public class OutStockController {

    @Autowired
    private OutStockService outStockService;

    /**
     * 购买接口
     *
     * @param id     商品ID
     * @param number 购买数量
     * @return 购买结果
     */
    @Anonymous
    @PostMapping("/buy")
    public String buy(@RequestParam Long id, @RequestParam Integer number) {
        // 调用 Service 完成购买逻辑
        return outStockService.updateStock(id, number);
    }
}
