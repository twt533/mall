package com.mall.data.service;

import com.mall.data.dto.DashboardDTO;
import com.mall.data.dto.OrderTrendDTO;
import com.mall.data.entity.ProductRanking;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
public class StatsService {

    /**
     * Get today's dashboard overview with realistic mock data.
     */
    public DashboardDTO getDashboard() {
        DashboardDTO dashboard = new DashboardDTO();
        Random random = new Random();
        dashboard.setTodayOrders(150 + random.nextInt(50));
        dashboard.setTodayAmount(new BigDecimal(String.format("%.2f", 45000.0 + random.nextDouble() * 15000)));
        dashboard.setTodayNewUsers(30 + random.nextInt(20));
        dashboard.setPendingOrders(12 + random.nextInt(15));
        dashboard.setTotalProducts(2500 + random.nextInt(200));
        dashboard.setTotalUsers(15800 + random.nextInt(500));
        return dashboard;
    }

    /**
     * Get order trend for last N days with mock data.
     */
    public List<OrderTrendDTO> getOrderTrend(int days) {
        List<OrderTrendDTO> trends = new ArrayList<>();
        LocalDate today = LocalDate.now();
        Random random = new Random();
        for (int i = days - 1; i >= 0; i--) {
            OrderTrendDTO trend = new OrderTrendDTO();
            trend.setDate(today.minusDays(i).toString());
            int baseOrders = 100 + random.nextInt(80);
            trend.setCount(baseOrders);
            trend.setAmount(new BigDecimal(String.format("%.2f", baseOrders * 280.0 + random.nextDouble() * 5000)));
            trends.add(trend);
        }
        return trends;
    }

    /**
     * Get product ranking by sales with mock data.
     */
    public List<ProductRanking> getProductRanking(int limit) {
        List<ProductRanking> rankings = new ArrayList<>();
        Random random = new Random();
        String[] productNames = {
            "iPhone 15 Pro Max", "华为 Mate 60 Pro", "MacBook Pro 14寸",
            "索尼 WH-1000XM5 耳机", "戴森 V15 吸尘器", "AirPods Pro 2代",
            "三星 Galaxy S24 Ultra", "iPad Air 5代", "小米14 Ultra",
            "任天堂 Switch OLED", "戴尔 U2723QE 显示器", "罗技 MX Master 3S 鼠标"
        };
        for (int i = 0; i < Math.min(limit, productNames.length); i++) {
            ProductRanking ranking = new ProductRanking();
            ranking.setStatDate(LocalDate.now());
            ranking.setProductId(1000L + i + 1);
            ranking.setProductName(productNames[i]);
            ranking.setSalesCount(500 - i * 35 + random.nextInt(30));
            ranking.setSalesAmount(new BigDecimal(String.format("%.2f", (500 - i * 35) * (1999.0 + random.nextDouble() * 8000))));
            ranking.setRankPosition(i + 1);
            rankings.add(ranking);
        }
        return rankings;
    }

    /**
     * Get user growth trend with mock data.
     */
    public List<Map<String, Object>> getUserGrowth(int days) {
        List<Map<String, Object>> growthList = new ArrayList<>();
        LocalDate today = LocalDate.now();
        Random random = new Random();
        for (int i = days - 1; i >= 0; i--) {
            Map<String, Object> item = new HashMap<>();
            item.put("date", today.minusDays(i).toString());
            item.put("newUsers", 20 + random.nextInt(40));
            item.put("activeUsers", 800 + random.nextInt(300));
            growthList.add(item);
        }
        return growthList;
    }
}
