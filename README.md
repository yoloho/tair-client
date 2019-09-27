tair-client
===
For tair 2.x ONLY.

# Supported commands
## Primary commands
`get`, `mget`, `getHidden`, `put`, `delete`, `mdelete`, `invalid`, `minvalid`, `hide`

`addItems`

`incr`, `decr`, `setCount`

`lock`, `unlock`, `mlock`, `munlock`, `append`, `getStat`

## Prefix based commands
`prefixGet`, `prefixGets`, `prefixPut`, `prefixPuts`, `prefixDelete`, `prefixDeletes`, 
`prefixIncr`, `prefixIncrs`, `prefixDecr`, `prefixDecrs`, `prefixSetCount`, `prefixSetCounts`,
`prefixGetHidden`, `prefixHide`, `prefixGetHiddens`, `prefixHides`, `prefixInvalid`, `prefixInvalids`,
`getRange`

# Change log
## 2.3.4.5
* Fix ttl doesn't take any effect when do prefix puts
* Security upgrading

## 2.3.4.4
* update getServer logic  
support for location first(same IDC to improve performance), load balance for reading