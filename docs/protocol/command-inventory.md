# NSOKISS command inventory

> Status: discovery artifact. Constant/value and Controller routing are VERIFIED from reference source. Payload and server-to-client direction remain UNKNOWN unless explicitly evidenced.

## Summary

- Constants declared in reference `CMD.java`: **311**.
- Constants routed by `Controller.java`: **126**.
- Declared constants not routed by this controller: **185**.
- Reused numeric byte values: **69 values**; collisions are expected because nested envelope scopes reuse the same signed byte space.
- DIRECT: **71** routed constants.
- NOT_LOGIN: **2** routed constants.
- NOT_MAP: **31** routed constants.
- SUB_COMMAND: **22** routed constants.

## Routing model

The outer `Controller.onMessage` accepts direct commands plus three envelope commands: `NOT_LOGIN`, `NOT_MAP`, and `SUB_COMMAND`. The nested methods read one additional command byte and dispatch it in their own scope. Therefore, a numeric value is not globally unique without its envelope/session phase.

| Scope | Reference handler | Preconditions observed | Meaning |
|---|---|---|---|
| DIRECT | `onMessage` | normally requires User + Char | in-map/gameplay commands and outer envelopes |
| NOT_LOGIN | `messageNotLogin` | User must be null | login/client metadata before account binding |
| NOT_MAP | `messageNotMap` | User exists; Char may be null | character select/create, data/template and post-login pre-map operations |
| SUB_COMMAND | `messageSubCommand` | outer guard normally requires User + Char | secondary gameplay/social/inventory operations |

## Interpretation rules

- `C→S VERIFIED`: the constant is present in a Controller case, so the client-to-server route is verified.
- `UNKNOWN`: no Controller case was found; it may be server-to-client, unused, handled elsewhere, or legacy.
- Handler evidence is a compact source expression, not a payload specification.
- Constant names are legacy reference symbols. Future NSOCry symbols must follow ADR-0005 and must not preserve legacy project identifiers.

## Routed commands

| Scope | Name | Value | Controller line | Handler evidence | Direction | Payload status |
|---|---|---:|---:|---|---|---|
| NOT_LOGIN | `LOGIN` | -127 | 406 | `client.login(mss);` | C→S VERIFIED | UNKNOWN |
| NOT_LOGIN | `CLIENT_INFO` | -125 | 410 | `client.setClientType(mss);` | C→S VERIFIED | UNKNOWN |
| NOT_MAP | `LAT_HINH` | -72 | 588 | `_char.selectCard(mss);` | C→S VERIFIED | UNKNOWN |
| NOT_MAP | `SELECT_PLAYER` | -126 | 435 | `user.selectChar(mss);` | C→S VERIFIED | UNKNOWN |
| NOT_MAP | `CREATE_PLAYER` | -125 | 450 | `user.createCharacter(mss);` | C→S VERIFIED | UNKNOWN |
| NOT_MAP | `UPDATE_DATA` | -122 | 456 | `service.updateData();` | C→S VERIFIED | UNKNOWN |
| NOT_MAP | `UPDATE_MAP` | -121 | 460 | `service.updateMap();` | C→S VERIFIED | UNKNOWN |
| NOT_MAP | `UPDATE_SKILL` | -120 | 464 | `service.updateSkill();` | C→S VERIFIED | UNKNOWN |
| NOT_MAP | `UPDATE_ITEM` | -119 | 468 | `service.updateItem();` | C→S VERIFIED | UNKNOWN |
| NOT_MAP | `REQUEST_ICON` | -115 | 472 | `service.requestIcon(mss);` | C→S VERIFIED | UNKNOWN |
| NOT_MAP | `REQUEST_CLAN_LOG` | -114 | 476 | `service.writeLog();` | C→S VERIFIED | UNKNOWN |
| NOT_MAP | `REQUEST_CLAN_INFO` | -113 | 481 | `service.requestClanInfo();` | C→S VERIFIED | UNKNOWN |
| NOT_MAP | `REQUEST_CLAN_MEMBER` | -112 | 487 | `service.requestClanMember();` | C→S VERIFIED | UNKNOWN |
| NOT_MAP | `REQUEST_CLAN_ITEM` | -111 | 493 | `service.requestClanItem();` | C→S VERIFIED | UNKNOWN |
| NOT_MAP | `REQUEST_MAPTEMPLATE` | -109 | 511 | `service.requestMapTemplate(mss);` | C→S VERIFIED | UNKNOWN |
| NOT_MAP | `REQUEST_NPCTEMPLATE` | -108 | 517 | `service.requestMobTemplate(mss);` | C→S VERIFIED | UNKNOWN |
| NOT_MAP | `CLIENT_OK` | -101 | 523 | `this.client.clientOk();` | C→S VERIFIED | UNKNOWN |
| NOT_MAP | `CLAN_CHANGE_ALERT` | -95 | 527 | `_char.changeClanAlert(mss);` | C→S VERIFIED | UNKNOWN |
| NOT_MAP | `CLAN_CHANGE_TYPE` | -94 | 533 | `_char.changeClanType(mss);` | C→S VERIFIED | UNKNOWN |
| NOT_MAP | `CLAN_MOVEOUT_MEM` | -93 | 539 | `_char.moveOutClan(mss);` | C→S VERIFIED | UNKNOWN |
| NOT_MAP | `CLAN_OUT` | -92 | 545 | `_char.outClan();` | C→S VERIFIED | UNKNOWN |
| NOT_MAP | `CLAN_UP_LEVEL` | -91 | 551 | `_char.clanUpLevel();` | C→S VERIFIED | UNKNOWN |
| NOT_MAP | `INPUT_COIN_CLAN` | -90 | 557 | `_char.inputCoinClan(mss);` | C→S VERIFIED | UNKNOWN |
| NOT_MAP | `OUTPUT_COIN_CLAN` | -89 | 563 | `_char.outputCoinClan(mss);` | C→S VERIFIED | UNKNOWN |
| NOT_MAP | `CONVERT_UPGRADE` | -88 | 576 | `_char.convertUpgrade(mss);` | C→S VERIFIED | UNKNOWN |
| NOT_MAP | `INVITE_CLANDUN` | -87 | 569 | `_char.inviteTerritory(mss);` | C→S VERIFIED | UNKNOWN |
| NOT_MAP | `ITEM_SPLIT` | -85 | 582 | `_char.inputNumberSplit(mss);` | C→S VERIFIED | UNKNOWN |
| NOT_MAP | `REWARD_PB` | -82 | 499 | `_char.rewardDungeon();` | C→S VERIFIED | UNKNOWN |
| NOT_MAP | `REWARD_CT` | -79 | 505 | `_char.rewardCT();` | C→S VERIFIED | UNKNOWN |
| NOT_MAP | `OPEN_CLAN_ITEM` | -62 | 594 | `_char.unlockClanItem();` | C→S VERIFIED | UNKNOWN |
| NOT_MAP | `CLAN_SEND_ITEM` | -61 | 600 | `_char.sendClanItem(mss);` | C→S VERIFIED | UNKNOWN |
| NOT_MAP | `CLAN_USE_ITEM` | -60 | 572 | `_char.clanUseItem(mss);` | C→S VERIFIED | UNKNOWN |
| NOT_MAP | `SERVER_ADD_MOB` | 122 | 441 | `byte type = mss.reader().readByte(); service.sendImgEffectAuto(mss); } else if (type == 1) {` | C→S VERIFIED | UNKNOWN |
| DIRECT | `SUB_COMMAND` | -30 | 61 | `messageSubCommand(mss);` | C→S VERIFIED | UNKNOWN |
| DIRECT | `NOT_LOGIN` | -29 | 57 | `messageNotLogin(mss);` | C→S VERIFIED | UNKNOWN |
| DIRECT | `NOT_MAP` | -28 | 53 | `messageNotMap(mss);` | C→S VERIFIED | UNKNOWN |
| DIRECT | `NEW_MESSAGE` | -109 | 45 | `newMessage(mss);` | C→S VERIFIED | UNKNOWN |
| DIRECT | `CHAT_MAP` | -23 | 123 | `_char.chatPublic(mss);` | C→S VERIFIED | UNKNOWN |
| DIRECT | `CHAT_PRIVATE` | -22 | 129 | `_char.chatPrivate(mss);` | C→S VERIFIED | UNKNOWN |
| DIRECT | `CHAT_SERVER` | -21 | 133 | `_char.chatGlobal(mss);` | C→S VERIFIED | UNKNOWN |
| DIRECT | `CHAT_PARTY` | -20 | 141 | `_char.chatParty(mss);` | C→S VERIFIED | UNKNOWN |
| DIRECT | `CHAT_CLAN` | -19 | 137 | `_char.chatClan(mss);` | C→S VERIFIED | UNKNOWN |
| DIRECT | `MAP_CHANGE` | -17 | 153 | `_char.requestChangeMap();` | C→S VERIFIED | UNKNOWN |
| DIRECT | `ITEMMAP_MYPICK` | -14 | 161 | `_char.pickItem(mss);` | C→S VERIFIED | UNKNOWN |
| DIRECT | `ME_THROW` | -12 | 157 | `_char.throwItem(mss);` | C→S VERIFIED | UNKNOWN |
| DIRECT | `ME_LIVE` | -10 | 169 | `_char.wakeUpFromDead(mss);` | C→S VERIFIED | UNKNOWN |
| DIRECT | `ME_BACK` | -9 | 165 | `_char.returnTownFromDead(mss);` | C→S VERIFIED | UNKNOWN |
| DIRECT | `PLAYER_MOVE` | 1 | 173 | `_char.move(mss);` | C→S VERIFIED | UNKNOWN |
| DIRECT | `PLAYER_ATTACK_N_P` | 4 | 321 | `_char.attackAllType(mss, 1);` | C→S VERIFIED | UNKNOWN |
| DIRECT | `ITEM_USE` | 11 | 182 | `_char.useItem(mss);` | C→S VERIFIED | UNKNOWN |
| DIRECT | `ITEM_USE_CHANGEMAP` | 12 | 187 | `_char.useItemChangeMap(mss);` | C→S VERIFIED | UNKNOWN |
| DIRECT | `ITEM_BUY` | 13 | 192 | `_char.buyItem(mss);` | C→S VERIFIED | UNKNOWN |
| DIRECT | `ITEM_SALE` | 14 | 197 | `_char.saleItem(mss);` | C→S VERIFIED | UNKNOWN |
| DIRECT | `ITEM_BODY_TO_BAG` | 15 | 202 | `_char.itemBodyToBag(mss);` | C→S VERIFIED | UNKNOWN |
| DIRECT | `ITEM_BOX_TO_BAG` | 16 | 207 | `_char.itemBoxToBag(mss);` | C→S VERIFIED | UNKNOWN |
| DIRECT | `ITEM_BAG_TO_BOX` | 17 | 212 | `_char.itemBagToBox(mss);` | C→S VERIFIED | UNKNOWN |
| DIRECT | `UPPEARL` | 19 | 227 | `_char.upPearl(mss, true);` | C→S VERIFIED | UNKNOWN |
| DIRECT | `UPPEARL_LOCK` | 20 | 232 | `_char.upPearl(mss, false);` | C→S VERIFIED | UNKNOWN |
| DIRECT | `UPGRADE` | 21 | 217 | `_char.upgradeItem(mss);` | C→S VERIFIED | UNKNOWN |
| DIRECT | `SPLIT` | 22 | 222 | `_char.splitItem(mss);` | C→S VERIFIED | UNKNOWN |
| DIRECT | `PLEASE_INPUT_PARTY` | 23 | 145 | `_char.pleaseInputParty(mss);` | C→S VERIFIED | UNKNOWN |
| DIRECT | `ACCEPT_PLEASE_PARTY` | 24 | 149 | `_char.acceptPleaseParty(mss);` | C→S VERIFIED | UNKNOWN |
| DIRECT | `REQUEST_PLAYERS` | 25 | 178 | `_char.requestCharInfo(mss);` | C→S VERIFIED | UNKNOWN |
| DIRECT | `ZONE_CHANGE` | 28 | 237 | `_char.changeZone(mss);` | C→S VERIFIED | UNKNOWN |
| DIRECT | `MENU` | 29 | 242 | `_char.menu(mss);` | C→S VERIFIED | UNKNOWN |
| DIRECT | `OPEN_UI_ZONE` | 36 | 246 | `service.openUIZone();` | C→S VERIFIED | UNKNOWN |
| DIRECT | `OPEN_UI_MENU` | 40 | 250 | `_char.openMenu(mss);` | C→S VERIFIED | UNKNOWN |
| DIRECT | `SKILL_SELECT` | 41 | 254 | `_char.selectSkill(mss);` | C→S VERIFIED | UNKNOWN |
| DIRECT | `REQUEST_ITEM_INFO` | 42 | 258 | `_char.requestItemInfo(mss);` | C→S VERIFIED | UNKNOWN |
| DIRECT | `TRADE_INVITE` | 43 | 262 | `_char.tradeInvite(mss);` | C→S VERIFIED | UNKNOWN |
| DIRECT | `TRADE_INVITE_ACCEPT` | 44 | 267 | `_char.acceptInviteTrade(mss);` | C→S VERIFIED | UNKNOWN |
| DIRECT | `TRADE_LOCK_ITEM` | 45 | 272 | `_char.tradeItemLock(mss);` | C→S VERIFIED | UNKNOWN |
| DIRECT | `TRADE_ACCEPT` | 46 | 277 | `_char.tradeAccept();` | C→S VERIFIED | UNKNOWN |
| DIRECT | `TASK_GET` | 47 | 282 | `_char.getTask(mss);` | C→S VERIFIED | UNKNOWN |
| DIRECT | `TRADE_CANCEL` | 57 | 286 | `_char.tradeClose();` | C→S VERIFIED | UNKNOWN |
| DIRECT | `FRIEND_INVITE` | 59 | 290 | `_char.addFriend(mss);` | C→S VERIFIED | UNKNOWN |
| DIRECT | `PLAYER_ATTACK_NPC` | 60 | 294 | `_char.attackMonster(mss);` | C→S VERIFIED | UNKNOWN |
| DIRECT | `PLAYER_ATTACK_PLAYER` | 61 | 299 | `_char.attackCharacter(mss);` | C→S VERIFIED | UNKNOWN |
| DIRECT | `TEST_INVITE` | 65 | 303 | `_char.testInvite(mss);` | C→S VERIFIED | UNKNOWN |
| DIRECT | `TEST_INVITE_ACCEPT` | 66 | 308 | `_char.testAccept(mss);` | C→S VERIFIED | UNKNOWN |
| DIRECT | `ADD_CUU_SAT` | 68 | 312 | `_char.addCuuSat(mss);` | C→S VERIFIED | UNKNOWN |
| DIRECT | `PLAYER_ATTACK_P_N` | 73 | 316 | `_char.attackAllType(mss, 2);` | C→S VERIFIED | UNKNOWN |
| DIRECT | `USE_SKILL_MY_BUFF` | 74 | 359 | `_char.useSkillBuff(mss);` | C→S VERIFIED | UNKNOWN |
| DIRECT | `PARTY_INVITE` | 79 | 343 | `_char.addParty(mss);` | C→S VERIFIED | UNKNOWN |
| DIRECT | `PARTY_ACCEPT` | 80 | 347 | `_char.addPartyAccept(mss);` | C→S VERIFIED | UNKNOWN |
| DIRECT | `PARTY_CANCEL` | 81 | 351 | `_char.addPartyCancel(mss);` | C→S VERIFIED | UNKNOWN |
| DIRECT | `PARTY_OUT` | 83 | 355 | `_char.outParty();` | C→S VERIFIED | UNKNOWN |
| DIRECT | `OPEN_TEXT_BOX_ID` | 92 | 326 | `_char.input(mss);` | C→S VERIFIED | UNKNOWN |
| DIRECT | `VIEW_INFO` | 93 | 330 | `_char.viewInfo(mss);` | C→S VERIFIED | UNKNOWN |
| DIRECT | `REQUEST_ITEM_PLAYER` | 94 | 334 | `_char.requestItemChar(mss);` | C→S VERIFIED | UNKNOWN |
| DIRECT | `TEST_DUN_INVITE` | 99 | 84 | `_char.acceptInviteTestDun(mss);` | C→S VERIFIED | UNKNOWN |
| DIRECT | `TEST_DUN_LIST` | 100 | 88 | `_char.requestMatchInfo(mss);` | C→S VERIFIED | UNKNOWN |
| DIRECT | `SEND_ITEM_TO_AUCTION` | 102 | 96 | `_char.sendToSaleItem(mss);` | C→S VERIFIED | UNKNOWN |
| DIRECT | `VIEW_ITEM_AUCTION` | 104 | 100 | `_char.requestViewDetails(mss);` | C→S VERIFIED | UNKNOWN |
| DIRECT | `BUY_ITEM_AUCTION` | 105 | 104 | `_char.buyItemAuction(mss);` | C→S VERIFIED | UNKNOWN |
| DIRECT | `OPEN_UI_CONFIRM_ID` | 107 | 92 | `_char.confirmID(mss);` | C→S VERIFIED | UNKNOWN |
| DIRECT | `ITEM_MON_TO_BAG` | 108 | 338 | `_char.itemMountToBag(mss);` | C→S VERIFIED | UNKNOWN |
| DIRECT | `LUYEN_THACH` | 110 | 118 | `_char.luyenThach(mss);` | C→S VERIFIED | UNKNOWN |
| DIRECT | `TINH_LUYEN` | 111 | 113 | `_char.tinhLuyen(mss);` | C→S VERIFIED | UNKNOWN |
| DIRECT | `DOI_OPTION` | 112 | 108 | `_char.dichChuyen(mss);` | C→S VERIFIED | UNKNOWN |
| DIRECT | `RANKED_MATCH` | 121 | 75 | `_char.requestRanked(mss);` | C→S VERIFIED | UNKNOWN |
| DIRECT | `NGOCKHAM` | 124 | 79 | `_char.ngocKham(mss);` | C→S VERIFIED | UNKNOWN |
| DIRECT | `GET_EFFECT` | 125 | 65 | `byte type = mss.reader().readByte(); service.sendImgEffect(mss); service.sendEffectData(mss);` | C→S VERIFIED | UNKNOWN |
| DIRECT | `REMOVE_VI_THU` | 117 | 49 | `_char.actionBijuu(mss);` | C→S VERIFIED | UNKNOWN |
| SUB_COMMAND | `POTENTIAL_UP` | -109 | 668 | `_char.upPotential(mss);` | C→S VERIFIED | UNKNOWN |
| SUB_COMMAND | `SKILL_UP` | -108 | 672 | `_char.upSkill(mss);` | C→S VERIFIED | UNKNOWN |
| SUB_COMMAND | `BAG_SORT` | -107 | 676 | `_char.bagSort();` | C→S VERIFIED | UNKNOWN |
| SUB_COMMAND | `BOX_SORT` | -106 | 680 | `_char.boxSort();` | C→S VERIFIED | UNKNOWN |
| SUB_COMMAND | `BOX_COIN_IN` | -105 | 684 | `_char.boxCoinIn(mss);` | C→S VERIFIED | UNKNOWN |
| SUB_COMMAND | `BOX_COIN_OUT` | -104 | 688 | `_char.boxCoinOut(mss);` | C→S VERIFIED | UNKNOWN |
| SUB_COMMAND | `REQUEST_ITEM` | -103 | 692 | `_char.requestItem(mss);` | C→S VERIFIED | UNKNOWN |
| SUB_COMMAND | `CHANGE_TYPE_PK` | -93 | 648 | `_char.changeTypePk(mss);` | C→S VERIFIED | UNKNOWN |
| SUB_COMMAND | `CREATE_PARTY` | -88 | 696 | `_char.createGroup();` | C→S VERIFIED | UNKNOWN |
| SUB_COMMAND | `CHANGE_TEAMLEADER` | -87 | 712 | `_char.changeTeamLeader(mss);` | C→S VERIFIED | UNKNOWN |
| SUB_COMMAND | `MOVE_MEMBER` | -86 | 708 | `_char.moveMember(mss);` | C→S VERIFIED | UNKNOWN |
| SUB_COMMAND | `REQUEST_FRIEND` | -85 | 660 | `service.requestFriend();` | C→S VERIFIED | UNKNOWN |
| SUB_COMMAND | `REQUEST_ENEMIES` | -84 | 664 | `service.requestEnemy();` | C→S VERIFIED | UNKNOWN |
| SUB_COMMAND | `FRIEND_REMOVE` | -83 | 652 | `_char.removeFriend(mss);` | C→S VERIFIED | UNKNOWN |
| SUB_COMMAND | `ENEMIES_REMOVE` | -82 | 656 | `_char.removeEnemy(mss);` | C→S VERIFIED | UNKNOWN |
| SUB_COMMAND | `BUFF_LIVE` | -79 | 631 | `_char.hoiSinh(mss);` | C→S VERIFIED | UNKNOWN |
| SUB_COMMAND | `FIND_PARTY` | -77 | 704 | `_char.openFindParty();` | C→S VERIFIED | UNKNOWN |
| SUB_COMMAND | `LOCK_PARTY` | -76 | 700 | `_char.lockParty(mss);` | C→S VERIFIED | UNKNOWN |
| SUB_COMMAND | `SAVE_RMS` | -67 | 644 | `_char.saveRms(mss);` | C→S VERIFIED | UNKNOWN |
| SUB_COMMAND | `LOAD_RMS` | -65 | 640 | `_char.loadSkillShortcut(mss);` | C→S VERIFIED | UNKNOWN |
| SUB_COMMAND | `CLAN_INVITE` | -63 | 627 | `_char.clanInvite(mss);` | C→S VERIFIED | UNKNOWN |
| SUB_COMMAND | `CLAN_ACCEPT_INVITE` | -62 | 636 | `_char.acceptInviteClan(mss);` | C→S VERIFIED | UNKNOWN |

## Declared but not routed by Controller

These constants are VERIFIED declarations only. Do not label them server-to-client until Service/client usage is traced.

| Name | Value | CMD.java line | Direction | Status |
|---|---:|---:|---|---|
| `REGISTER` | -126 | 6 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `SEND_SMS` | -124 | 8 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `REGISTER_IMEI` | -123 | 9 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `LOGIN0` | 0 | 10 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `REGISTER0` | 1 | 11 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `CLEAR_RMS` | 2 | 12 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `FORGET_PASS` | -122 | 13 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `FORGET_PASS_IMEI` | -121 | 14 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `CHECK_KEY1` | -76 | 15 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `CHECK_KEY2` | -75 | 16 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `CHECK_KEY3` | -74 | 17 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `CHECK_KEY4` | -73 | 18 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `CLEAR_TRANSACTION_ID` | -71 | 20 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `MOI_GTC` | -70 | 21 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `MOI_TATCA_GTC` | -69 | 22 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `GO_GTCHIEN` | -68 | 23 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `LOGOUT` | -127 | 24 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `DELETE_PLAYER` | -124 | 27 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `UPDATE_VERSION` | -123 | 28 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `UPDATE_VERSION_OK` | -118 | 33 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `UPDATE_PK` | -117 | 34 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `UPDATE_OUT_CLAN` | -116 | 35 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `REQUEST_SKILL` | -110 | 41 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `REQUEST_NPCPLAYER` | -107 | 44 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `ME_LOAD_ACTIVE` | -106 | 45 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `ME_ACTIVE` | -105 | 46 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `ME_UPDATE_ACTIVE` | -104 | 47 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `ME_OPEN_LOCK` | -103 | 48 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `ME_CLEAR_LOCK` | -102 | 49 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `CLIENT_OK_INMAP` | -100 | 51 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `INPUT_CARD` | -99 | 52 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `CLEAR_TASK` | -98 | 53 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `CHANGE_NAME` | -97 | 54 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `CREATE_CLAN` | -96 | 55 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `NOT_USEACC` | -86 | 65 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `POINT_PB` | -84 | 67 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `REVIEW_PB` | -83 | 68 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `CHIENTRUONG_INFO` | -81 | 70 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `REVIEW_CT` | -80 | 71 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `CHAT_ADMIN` | -78 | 73 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `CHANGE_BG_ID` | -77 | 74 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `OAN_HON` | -67 | 75 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `OAN_HON1` | -66 | 76 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `NAP_NOKIA` | -65 | 77 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `GET_PASS2` | -64 | 78 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `NAP_GOOGLE` | -63 | 79 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `GPS` | -59 | 83 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `ME_LOAD_ALL` | -127 | 84 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `ME_LOAD_CLASS` | -126 | 85 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `ME_LOAD_SKILL` | -125 | 86 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `ME_LOAD_LEVEL` | -124 | 87 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `ME_LOAD_INFO` | -123 | 88 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `ME_LOAD_HP` | -122 | 89 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `ME_LOAD_MP` | -121 | 90 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `PLAYER_LOAD_ALL` | -120 | 91 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `PLAYER_LOAD_INFO` | -119 | 92 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `PLAYER_LOAD_LEVEL` | -128 | 93 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `PLAYER_LOAD_VUKHI` | -117 | 94 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `PLAYER_LOAD_AO` | -116 | 95 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `PLAYER_LOAD_QUAN` | -113 | 96 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `PLAYER_LOAD_BODY` | -112 | 97 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `PLAYER_LOAD_HP` | -111 | 98 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `PLAYER_LOAD_LIVE` | -110 | 99 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `USE_BOOK_SKILL` | -102 | 107 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `ME_ADD_EFFECT` | -101 | 108 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `ME_EDIT_EFFECT` | -100 | 109 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `ME_REMOVE_EFFECT` | -99 | 110 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `PLAYER_ADD_EFFECT` | -98 | 111 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `PLAYER_EDIT_EFFECT` | -97 | 112 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `PLAYER_REMOVE_EFFECT` | -96 | 113 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `MAP_TIME` | -95 | 114 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `NPC_PLAYER_UPDATE` | -94 | 115 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `UPDATE_TYPE_PK` | -92 | 117 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `UPDATE_BAG_COUNT` | -91 | 118 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `TASK_FOLLOW_FAIL` | -90 | 119 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `END_WAIT` | -89 | 120 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `ME_UPDATE_PK` | -81 | 128 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `ITEM_BODY_CLEAR` | -80 | 129 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `CALL_EFFECT_ME` | -78 | 131 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `ITEM_BOX_CLEAR` | -75 | 134 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `SHOW_WAIT` | -74 | 135 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `CALL_EFFECT_NPC` | -73 | 136 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `ME_LOAD_GOLD` | -72 | 137 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `ME_UP_GOLD` | -71 | 138 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `ADMIN_MOVE` | -70 | 139 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `ME_LOAD_THU_NUOI` | -69 | 140 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `PLAYER_LOAD_THU_NUOI` | -68 | 141 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `PLAYER_LOAD_MAT_NA` | -64 | 144 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `CLAN_PLEASE` | -61 | 147 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `CLAN_ACCEPT_PLEASE` | -60 | 148 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `REFRESH_HP` | -59 | 149 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `CALL_EFFECT_BALL` | -58 | 150 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `CALL_EFFECT_BALL_1` | -57 | 151 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `PLAYER_LOAD_AO_CHOANG` | -56 | 152 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `PLAYER_LOAD_GIA_TOC` | -55 | 153 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `LOAD_THU_CUOI` | -54 | 154 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `FULL_SIZE` | -32 | 155 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `KEY_WINPHONE` | -31 | 156 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `GET_SESSION_ID` | -27 | 161 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `SERVER_DIALOG` | -26 | 162 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `SERVER_ALERT` | -25 | 163 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `SERVER_MESSAGE` | -24 | 164 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `MAP_INFO` | -18 | 170 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `MAP_CLEAR` | -16 | 172 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `ITEMMAP_REMOVE` | -15 | 173 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `ITEMMAP_PLAYERPICK` | -13 | 175 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `ME_DIE` | -11 | 177 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `ME_UP_COIN_LOCK` | -8 | 180 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `ME_CHANGE_COIN` | -7 | 181 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `PLAYER_THROW` | -6 | 182 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `NPC_LIVE` | -5 | 183 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `NPC_DIE` | -4 | 184 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `NPC_ATTACK_ME` | -3 | 185 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `NPC_ATTACK_PLAYER` | -2 | 186 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `NPC_HP` | -1 | 187 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `PLAYER_DIE` | 0 | 188 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `PLAYER_REMOVE` | 2 | 190 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `PLAYER_ADD` | 3 | 191 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `PLAYER_UP_EXP` | 5 | 193 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `ITEMMAP_ADD` | 6 | 194 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `ITEM_BAG_REFRESH` | 7 | 195 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `ITEM_BAG_ADD` | 8 | 196 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `ITEM_BAG_ADD_QUANTITY` | 9 | 197 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `ITEM_BAG_CLEAR` | 10 | 198 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `ITEM_USE_UPTOUP` | 18 | 206 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `UPDATE_ACHIEVEMENT` | 26 | 214 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `MOVE_FAST_NPC` | 27 | 215 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `OPEN_UI` | 30 | 218 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `OPEN_UI_BOX` | 31 | 219 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `OPEN_UI_PT` | 32 | 220 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `OPEN_UI_SHOP` | 33 | 221 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `OPEN_MENU_ID` | 34 | 222 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `OPEN_UI_COLLECT` | 35 | 223 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `OPEN_UI_TRADE` | 37 | 225 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `OPEN_UI_SAY` | 38 | 226 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `OPEN_UI_CONFIRM` | 39 | 227 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `TASK_NEXT` | 48 | 236 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `TASK_FINISH` | 49 | 237 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `TASK_UPDATE` | 50 | 238 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `NPC_MISS` | 51 | 239 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `RESET_POINT` | 52 | 240 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `ALERT_MESSAGE` | 53 | 241 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `ALERT_OPEN_WEB` | 54 | 242 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `ALERT_SEND_SMS` | 55 | 243 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `TRADE_INVITE_CANCEL` | 56 | 244 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `TRADE_OK` | 58 | 246 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `HAVE_ATTACK_PLAYER` | 62 | 250 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `OPEN_UI_NEWMENU` | 63 | 252 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `MOVE_FAST` | 64 | 253 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `TEST_END` | 67 | 256 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `ME_CUU_SAT` | 69 | 258 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `CLEAR_CUU_SAT` | 70 | 259 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `PLAYER_UP_EXPDOWN` | 71 | 260 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `ME_DIE_EXP_DOWN` | 72 | 261 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `CREATE_BUNHIN` | 75 | 264 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `NPC_ATTACK_BUNHIN` | 76 | 265 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `CLEAR_BUNHIN` | 77 | 266 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `NPC_CHANGE` | 78 | 267 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `PLAYER_IN_PARTY` | 82 | 271 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `FRIEND_ADD` | 84 | 273 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `NPC_IS_DISABLE` | 85 | 274 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `NPC_IS_MOVE` | 86 | 275 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `ThuNuoi_ATTACK` | 87 | 276 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `RETURN_POINT_MAP` | 88 | 277 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `NPC_IS_FIRE` | 89 | 278 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `NPC_IS_ICE` | 90 | 279 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `NPC_IS_WIND` | 91 | 280 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `ME_UP_COIN_BAG` | 95 | 284 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `GET_TASK_ORDER` | 96 | 285 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `GET_TASK_UPDATE` | 97 | 286 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `CLEAR_TASK_ORDER` | 98 | 287 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `VIEW_INFO1` | 101 | 290 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `LOAD_ITEM_AUCTION` | 103 | 292 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `TEST_GT_INVITE` | 106 | 295 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `OPEN_UI_MENU1` | 109 | 298 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `CAT_KEO` | 113 | 302 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `NV_BIAN` | 114 | 303 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `UPDATE_INFO_ME` | 115 | 304 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `UPDATE_INFO_CHAR` | 116 | 305 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `MAP_ITEM` | 117 | 306 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `COMFIRM_ACCOUNT` | 118 | 307 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `AUTO_ATTACK_MOVE` | 119 | 308 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `DOI_MAT_KHAU` | 120 | 309 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `info_kiemduyet` | 123 | 312 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |
| `GIAODO` | 126 | 315 | UNKNOWN | declaration VERIFIED; routing UNKNOWN |

## Numeric value collisions

A collision is not automatically a bug. The envelope/scope and session phase are part of command identity.

| Value | Symbols |
|---:|---|
| -127 | `LOGIN`, `LOGOUT`, `ME_LOAD_ALL` |
| -126 | `REGISTER`, `SELECT_PLAYER`, `ME_LOAD_CLASS` |
| -125 | `CLIENT_INFO`, `CREATE_PLAYER`, `ME_LOAD_SKILL` |
| -124 | `SEND_SMS`, `DELETE_PLAYER`, `ME_LOAD_LEVEL` |
| -123 | `REGISTER_IMEI`, `UPDATE_VERSION`, `ME_LOAD_INFO` |
| -122 | `FORGET_PASS`, `UPDATE_DATA`, `ME_LOAD_HP` |
| -121 | `FORGET_PASS_IMEI`, `UPDATE_MAP`, `ME_LOAD_MP` |
| -120 | `UPDATE_SKILL`, `PLAYER_LOAD_ALL` |
| -119 | `UPDATE_ITEM`, `PLAYER_LOAD_INFO` |
| -117 | `UPDATE_PK`, `PLAYER_LOAD_VUKHI` |
| -116 | `UPDATE_OUT_CLAN`, `PLAYER_LOAD_AO` |
| -113 | `REQUEST_CLAN_INFO`, `PLAYER_LOAD_QUAN` |
| -112 | `REQUEST_CLAN_MEMBER`, `PLAYER_LOAD_BODY` |
| -111 | `REQUEST_CLAN_ITEM`, `PLAYER_LOAD_HP` |
| -110 | `REQUEST_SKILL`, `PLAYER_LOAD_LIVE` |
| -109 | `REQUEST_MAPTEMPLATE`, `POTENTIAL_UP`, `NEW_MESSAGE` |
| -108 | `REQUEST_NPCTEMPLATE`, `SKILL_UP` |
| -107 | `REQUEST_NPCPLAYER`, `BAG_SORT` |
| -106 | `ME_LOAD_ACTIVE`, `BOX_SORT` |
| -105 | `ME_ACTIVE`, `BOX_COIN_IN` |
| -104 | `ME_UPDATE_ACTIVE`, `BOX_COIN_OUT` |
| -103 | `ME_OPEN_LOCK`, `REQUEST_ITEM` |
| -102 | `ME_CLEAR_LOCK`, `USE_BOOK_SKILL` |
| -101 | `CLIENT_OK`, `ME_ADD_EFFECT` |
| -100 | `CLIENT_OK_INMAP`, `ME_EDIT_EFFECT` |
| -99 | `INPUT_CARD`, `ME_REMOVE_EFFECT` |
| -98 | `CLEAR_TASK`, `PLAYER_ADD_EFFECT` |
| -97 | `CHANGE_NAME`, `PLAYER_EDIT_EFFECT` |
| -96 | `CREATE_CLAN`, `PLAYER_REMOVE_EFFECT` |
| -95 | `CLAN_CHANGE_ALERT`, `MAP_TIME` |
| -94 | `CLAN_CHANGE_TYPE`, `NPC_PLAYER_UPDATE` |
| -93 | `CLAN_MOVEOUT_MEM`, `CHANGE_TYPE_PK` |
| -92 | `CLAN_OUT`, `UPDATE_TYPE_PK` |
| -91 | `CLAN_UP_LEVEL`, `UPDATE_BAG_COUNT` |
| -90 | `INPUT_COIN_CLAN`, `TASK_FOLLOW_FAIL` |
| -89 | `OUTPUT_COIN_CLAN`, `END_WAIT` |
| -88 | `CONVERT_UPGRADE`, `CREATE_PARTY` |
| -87 | `INVITE_CLANDUN`, `CHANGE_TEAMLEADER` |
| -86 | `NOT_USEACC`, `MOVE_MEMBER` |
| -85 | `ITEM_SPLIT`, `REQUEST_FRIEND` |
| -84 | `POINT_PB`, `REQUEST_ENEMIES` |
| -83 | `REVIEW_PB`, `FRIEND_REMOVE` |
| -82 | `REWARD_PB`, `ENEMIES_REMOVE` |
| -81 | `CHIENTRUONG_INFO`, `ME_UPDATE_PK` |
| -80 | `REVIEW_CT`, `ITEM_BODY_CLEAR` |
| -79 | `REWARD_CT`, `BUFF_LIVE` |
| -78 | `CHAT_ADMIN`, `CALL_EFFECT_ME` |
| -77 | `CHANGE_BG_ID`, `FIND_PARTY` |
| -76 | `CHECK_KEY1`, `LOCK_PARTY` |
| -75 | `CHECK_KEY2`, `ITEM_BOX_CLEAR` |
| -74 | `CHECK_KEY3`, `SHOW_WAIT` |
| -73 | `CHECK_KEY4`, `CALL_EFFECT_NPC` |
| -72 | `LAT_HINH`, `ME_LOAD_GOLD` |
| -71 | `CLEAR_TRANSACTION_ID`, `ME_UP_GOLD` |
| -70 | `MOI_GTC`, `ADMIN_MOVE` |
| -69 | `MOI_TATCA_GTC`, `ME_LOAD_THU_NUOI` |
| -68 | `GO_GTCHIEN`, `PLAYER_LOAD_THU_NUOI` |
| -67 | `OAN_HON`, `SAVE_RMS` |
| -65 | `NAP_NOKIA`, `LOAD_RMS` |
| -64 | `GET_PASS2`, `PLAYER_LOAD_MAT_NA` |
| -63 | `NAP_GOOGLE`, `CLAN_INVITE` |
| -62 | `OPEN_CLAN_ITEM`, `CLAN_ACCEPT_INVITE` |
| -61 | `CLAN_SEND_ITEM`, `CLAN_PLEASE` |
| -60 | `CLAN_USE_ITEM`, `CLAN_ACCEPT_PLEASE` |
| -59 | `GPS`, `REFRESH_HP` |
| 0 | `LOGIN0`, `PLAYER_DIE` |
| 1 | `REGISTER0`, `PLAYER_MOVE` |
| 2 | `CLEAR_RMS`, `PLAYER_REMOVE` |
| 117 | `MAP_ITEM`, `REMOVE_VI_THU` |

## Known payload evidence from Controller

Only the following payload fragments are explicit at the routing layer:

- `NOT_LOGIN/CLIENT_INFO` delegates to `Session.setClientType`; byte-level layout must be documented from that method.
- `NOT_LOGIN/LOGIN` delegates to `Session.login`; credentials/login layout must be documented from that method.
- `NOT_MAP/SERVER_ADD_MOB` reads a leading `type` byte and routes type 0/1 to different effect-auto responses.
- `DIRECT/GET_EFFECT` reads a leading `type` byte and routes type 1/2 to image/effect-data responses.
- `NEW_MESSAGE` has a nested raw byte switch; subcommand 0 calls clan cancellation and is not represented by a named CMD constant.

## Gaps and next analysis

1. Trace `Session.setClientType` and `Session.login` byte by byte.
2. Trace `Session.MessageCollector` to document envelope decoding/key state.
3. Search `Service.java`, `AbsService.java`, `GlobalService.java` and game classes for every unhandled constant to classify S→C/unused/elsewhere.
4. Cross-check the client JAR and capture fixtures before naming payload fields as VERIFIED.
5. Produce NSOCry command names only after responsibilities are understood; do not copy legacy names blindly.

## Evidence

- `source-reference/NSOKISS-inspection/src/main/java/com/nsoz/constants/CMD.java`
- `source-reference/NSOKISS-inspection/src/main/java/com/nsoz/network/Controller.java`
