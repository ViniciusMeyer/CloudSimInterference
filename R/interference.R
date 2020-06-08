#capacityCost ---- if pm cap < given(tiers_size) ? 100000 : 1

#interferenceCost ---- recieve tier(cpu_deg, cache_deg etc)
#                 ----   receive one tier(CPU)
#                 ----    if(CPU > 1) ? cost*= cpu_deg etc, count++ : 1                   
#                 ----    return if(count > 1) ? cost :  1
#                 ----return intCost(cpu) * intCost(memory) * etc... 


