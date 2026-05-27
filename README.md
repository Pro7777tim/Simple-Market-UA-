# Simple Market [UA]

# _English_

A simple Minecraft Forge mod that simulates a market/exchange/economy

## Additional information
* Version - minecraft 1.20.1 forge
* License - GPL_3
* Language - English, Ukrainian
* Environment - client/server (required)

## Features
* Items that can be bought or sold display hints about the price and the initial buy/sell price
* Each player has their own account. Config file - ```market-balances.json```
* Demand system for selling. Every minute, demand/price increases. When something is sold, demand/price decreases proportionally to the amount of the item sold. Config file: ```market-state.json```
* Demand system for buying. Every minute, demand/price decreases. When something is bought, demand/price increases relative to the amount of goods purchased. Config file - ```market-state.json```
* Convenient buy/sell menus where you can view your balance and buy/sell demand, browse pages, and buy/sell goods by finding them in the menu and clicking on them
* You can edit the list of items for sale/purchase in the configuration file - ```market-common.toml``` -> json array sell_items/buy_items. Product listing: [..., "*product_name=price=per_unit={nbt_tags}(for purchase only)*"], For example: [..., "superbwarfare:container=150=true={BlockEntityTag:{EntityType:\"superbwarfare:yx_100\"}}"]

## Commands
* ```/market reload``` - reload the configuration
* ```/market money``` - view your account
* ```/market pay *player* *amount*``` - transfer money to a player
* ```/market buy``` - shopping menu
* ```/market sell``` - sales menu

## Support
I created this mod for my own use, but I decided to publish it. Updates will be released from time to time, and bugs will be fixed.
[Developer](https://discord.com/invite/C4UYWPtCzz) [Modrinth](https://modrinth.com/project/O2MZyBNe)

# _Ukrainian_

Простий minecraft forge мод, який емулює ринок/біржу/економіку

## Додаткова інформація
* Версія - minecraft 1.20.1 forge
* Ліцензія - GPL_3
* Мова - Англійська, Українська
* Середовище існування - клієнт/сервер (обов'язково)

## Функціонал
* На товарах які можна купити/продати, з'являються підказки про ціну та початкову ціну купівлі/продажу
* Кожен гравець має свій рахунок. Конфіг файл - ```market-balances.json```
* Система попиту для продажу. Кожну хвилину попит/ціна росте. Щось продано - попит/ціна падає відносно від суми проданого товару. Конфіг файл - ```market-state.json```
* Система попиту для купівлі. Кожну хвилину попит/ціна падає. Щось куплено - попит/ціна росте відносно від суми купленого товару. Конфіг файл - ```market-state.json```
* Зручні меню продажу/купівлі, де можна подивитися гроші на балансу та попит на продаж/купівлю, погортати сторінки і продати/купити товар, знайшовши його у меню і клікнувши по ньому
* Можна редагувати список товарів для продажу/купівлі у конфіг файлі - ```market-common.toml``` -> json список sell_items/buy_items. Запис товару: [..., "*назва_пердмету=ціна=за_штуку={nbt_теги}(тільки для купівлі)*"], Наприклад: [..., "superbwarfare:container=150=true={BlockEntityTag:{EntityType:\"superbwarfare:yx_100\"}}"]

## Команди
* ```/market reload``` - перезавантажити конфіг
* ```/market money``` - переглянути рахунок
* ```/market pay *гравець* *сума*``` - переказати гроші гравцю
* ```/market buy``` - меню купівлі
* ```/market sell``` - меню продажу

## Підтримка
Мод був зроблений для свого використання, але я його вирішив опублікувати. Оновлення іноді будуть виходити а баги будуть фікситися.
[Розробник](https://discord.com/invite/C4UYWPtCzz) [Modrinth](https://modrinth.com/project/O2MZyBNe)