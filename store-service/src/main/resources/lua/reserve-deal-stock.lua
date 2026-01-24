local reservationZset = KEYS[1]   -- product:{productId}:reservations
local reservedKey = KEYS[2]       -- product:{productId}:reserved
local qtyHash = KEYS[3]           -- product:{productId}:qty
local now = ARGV[1]

local expired = redis.call('ZRANGEBYSCORE', reservationZset, '-inf', now)

for _, orderId in ipairs(expired) do
    local qty = tonumber(redis.call('HGET', qtyHash, orderId))
    if qty then
        redis.call('DECRBY', reservedKey, qty)
        redis.call('HDEL', qtyHash, orderId)
    end
end

redis.call('ZREMRANGEBYSCORE', reservationZset, '-inf', now)
return #expired