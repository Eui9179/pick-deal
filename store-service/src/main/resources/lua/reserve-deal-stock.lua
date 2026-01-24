-- KEYS[1]: deal:reservations:{dealId} (ZSET 키)
-- KEYS[2]: deal:totalQty:{dealId} 현재 예약된 개수
-- KEYS[3]: order:qty:{orderId} (주문 당 재고 개수)
-- ARGV[1]: stockQuantity (현재 재고)
-- ARGV[2]: orderId (주문 ID)
-- ARGV[3]: requestQty (주문 요청 수량)
-- ARGV[4]: expiredAt (만료 시간 milliseconds)
-- ARGV[5]: currentTime (현재 시간 milliseconds)

local zSetKey = KEYS[1]
local reservedQtyKey = KEYS[2] -- Redis에 있는 결제 완료되지 않은 재고 개수
local orderKeyPrefix = KEYS[3]

local totalQty = ARGV[1] -- DB에 있는 재고 개수
local requestOrderId = ARGV[2] -- 결제 대기 중인 주문 ID
local requestQty = tonumber(ARGV[3]) -- 결제 대기 중인 재고 개수
local expiredAt = ARGV[4] -- 결제 Timeout timestamp
local currentTime = ARGV[5]

-- 1. zSet에 있는 주문 TTL 만료된 데이터 조회
-- expired = [{"order:001" : timestamp}, "{order:002 : timestamp}", ...]
local expired = redis.call('ZRANGEBYSCORE', zSetKey, '-inf', currentTime)

-- 2. 만료된 데이터의 재고 합산만큼 총 재고 감소
for _, orderId in ipairs(expired) do
    local qty = tonumber(redis.call('GET', orderKeyPrefix .. orderId))
    if qty then
        redis.call('DECRBY', reservedQtyKey, qty)
        redis.call('DEL', orderKeyPrefix .. orderId)
    end
end

redis.call('ZREMRANGEBYSCORE', zSetKey, '-inf', currentTime)

local crtReservedQty = tonumber(redis.call('GET', reservedQtyKey)) or 0

if totalQty - crtReservedQty < requestQty then
    return -1
end

-- 3. 요청 온 데이터 추가
redis.call('ZADD', zSetKey, expiredAt, requestOrderId)
redis.call('INCRBY', reservedQtyKey, requestQty)
redis.call('SET', orderKeyPrefix .. requestOrderId, requestQty)

return crtReservedQty + requestQty
