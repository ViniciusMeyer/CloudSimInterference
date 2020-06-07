app_from_list <- function(list, app_num) {
  i_netp <- 1
  i_nets <- 2
  i_blk <- 3
  i_mbw <- 4
  i_llcmr <- 5
  i_llcocc <- 6
  i_cpu <- 7
  df_app <- data.frame()
  
  df_app <- data.frame(list[[(app_num * 7 + i_netp)]],
                       list[[(app_num * 7 + i_nets)]],
                       list[[(app_num * 7 + i_blk)]],
                       list[[(app_num * 7 + i_mbw)]],
                       list[[(app_num * 7 + i_llcmr)]],
                       list[[(app_num * 7 + i_llcocc)]],
                       list[[(app_num * 7 + i_cpu)]])
  df_app <-
    setNames(df_app,
             c("netp", "nets", "blk", "mbw", "llcmr", "llcocc", "cpu"))
  return(df_app)
}


commom_interval <- function(interval_list, app_list) {
  interval <- vector()
  interval_aux2 <- vector()
  interval_aux <- list()
  
  for (i in 1:length(interval_list)) {
    for (j in 1:length(interval_list[[i]])) {
      if (!interval_list[[i]][j] %in% interval) {
        interval <- c(interval, interval_list[[i]][j])
      }
    }
  }
  
  interval <- sort(interval, decreasing = FALSE)
  for (i in 1:length(interval_list)) {
    for (j in 1:length(interval)) {
      if (interval[j] <= max(interval_list[[i]])) {
        interval_aux2<-c(interval_aux2, interval[j])
      }
    }
  #print(interval_aux2)
    interval_aux <- c(interval_aux, list(interval_aux2))
    interval_aux2 <- vector()
  }
  
  return(interval_aux)
}
