*~~目前所有材质初始皆套用原版材质~~*
## 物品 *Items*

### 1. 收集物

- 侵蚀碎片 <br>
  初级基础素材，可通过受侵蚀方块上获得，可用于精炼
   
### 2. 装备 
- 基础侵蚀套装

## 方块 *Blocks*
### 1. 侵蚀方块 *Eroded Blocks*
一类受到侵蚀的原版方块，拥有和玻璃相似的材质与声音，不会提供通常的掉落物。

- **侵蚀草方块 *Eroded Grass Blocks***
- **侵蚀泥土 *Eroded Dirt***

### 2. 环境方块 *Env Blocks*
一类由侵蚀环境生成的方块

- **微光核心 *Shimmer Core***
  <br>激活状态时亮度为 12
  会使周围玩家侵蚀度上升
- **微光柱（方块） *Shimmer Pillar***
  <br>激活状态时亮度为 6

## 地物 *Feature*
### 1. 微光柱 *Shimmer Pillar*
散落在各个群系上的自然生长结构。中心拥有一块被微光柱包裹的微光核心，

## 状态 *StatusEffect*

### 1. 侵蚀 Erosion

## 玩家（实体） *PlayerEntity*

### 1. 属性
- **侵蚀度/侵蚀等级**：

## 规则 *Gamerule*
### 1. 侵蚀选择器 *Erosion Selector*
  - *ALL:可对所有模组对象产生侵蚀效果*
  - *ORIGIN:仅对原版和本模组对象产生侵蚀效果*
  - *OTHER:没有具体含义，用于扩展使用（没有扩展定义则效果与 ORIGIN 一样）*
  - *NONE:不选择任何对象（即禁用）*

## 命令 *Command*

### Erosion 命令

- `/lee erosion` `/lee erosion help` 查看帮助
- `/lee erosion data [<player>]` 查看角色侵蚀数据
- `/lee erosion clear [<player>]` 清除侵蚀度
- `/lee erosion clean [<player>]` 完全清除侵蚀度
- `/lee erosion add <value> [<player>]` 增加侵蚀值（可为负值）
- `/lee erosion level [up|down] [progress|clean|keep] [<player>]` 提升或者降低等级

------
### 计划
- 增添侵蚀相关群系
  1. **侵蚀群系体系**：高侵蚀度区域（计划出现各种地形），几乎不会出现非本模组结构，并且会出现各种高侵蚀度现象（玩家、刷怪、侵蚀结构、自然结构等）
  2. **失落群系**：特殊的高侵蚀度区域，并会出现类似城堡的大型结构（贴合侵蚀国度主题）
  
- 侵蚀方块的设计不在采用全新的方块类，而是给方块新增属性和一些独特的附着行方块

- 【重要】找到如何生成全新群系

------
### 日志
- 2023-06-06 初步完成微光柱地物（中等未激活型）~~整整一个月去参考地物怎么写.jpg~~ 
- 2023-06-09 给地物判定添加一个下边界，避免末地维度卡死。~~我错怪维度生成了~~
- 2023-06-28 
  1. 完成玩家数据扩展
  2. 玩家靠近地微光柱就会被侵蚀（即和玩家数据——侵蚀数据互动）
  3. 微光柱将不在依靠 *Status Effect* 便能对玩家产生影响（拥有独自逻辑）
  4. 添加了画面显示侵蚀数据HUD

- 2023-07-04
  1. 搞定指令树（erosion）
  
- 2023-07-09
  0. 我终于知道群系是怎么生成了（它竟然写死了）