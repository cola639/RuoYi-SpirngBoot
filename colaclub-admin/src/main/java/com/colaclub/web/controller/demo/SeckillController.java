//@RestController
//@RequestMapping("/seckill")
//public class SeckillController {
//
//    private static final String SECKILL_KEY = "seckill:goods";
//    private static final int LIMIT = 5; // 活动时间限制 5 分钟
//    @Autowired
//    private RedisCache redisCache;
//    @Autowired
//    private ExampleService exampleService;
//
//    @PostMapping("/participate")
//    public ResponseEntity<String> participate(@RequestParam String userId, @RequestParam String goodsId) {
//        // 检查活动时间
//        if (!isWithinActivityPeriod()) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("活动未开始或已结束");
//        }
//
//        // 检查库存
//        Integer stock = redisCache.getCacheObject(SECKILL_KEY + ":" + goodsId);
//        if (stock == null || stock <= 0) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("商品已售罄");
//        }
//
//        // 分布式锁
//        String lockKey = "lock:" + userId + ":" + goodsId;
//        boolean locked = redisCache.lock(lockKey, 5, TimeUnit.SECONDS);
//        if (!locked) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("请求过于频繁，请稍后再试");
//        }
//
//        try {
//            // 预减库存
//            redisCache.decrement(SECKILL_KEY + ":" + goodsId);
//
//            // 异步下单
//            exampleService.createOrderAsync(userId, goodsId);
//
//            return ResponseEntity.ok("抢购成功");
//        } catch (Exception e) {
//            redisCache.increment(SECKILL_KEY + ":" + goodsId); // 恢复库存
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("抢购失败，请稍后再试");
//        } finally {
//            redisCache.unlock(lockKey);
//        }
//    }
//
//    private boolean isWithinActivityPeriod() {
//        LocalTime now = LocalTime.now();
//        LocalTime startTime = LocalTime.of(23, 0);
//        LocalTime endTime = LocalTime.of(23, 5);
//        return !now.isBefore(startTime) && !now.isAfter(endTime);
//    }
//}
