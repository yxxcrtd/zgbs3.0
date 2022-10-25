--这个脚本要在20220916_组合包自动关联规则初始化.sql 后执行,初始化时使用，已有自建的组合包时不可执行
delete from RSK_PORTFOLIO_PACKAGE_DTL where  PTL_BAG_ID not in ('47a18728d1da47248ea7d039ab9f0291','5bb90798421a44e988f08f8cacc4d5d3','d01681618e4948e19b1619ad30c7cfe2')
