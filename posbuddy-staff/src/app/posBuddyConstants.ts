export abstract class PosBuddyConstants {
  // max payment in one transaction should beware of typos
  static readonly MAX_DEPOSIT = 250.0;

  // -- ID NOT SET
  static readonly INVALID_POSBUDDY_ID = "";

  static readonly DEPOSIT = "+";
  static readonly PAYMENT = "-";

  static readonly DISPENSING_STATION_FILTER_PRE = "stationFilter_";

  static readonly REPORT_TYPE_ONE_TIME = "ONE_TIME_ID";

  static readonly CARD_TYPE_BORROW = "borrowCard";
  static readonly CARD_TYPE_ONE_TIME = "oneTimeCard";
  static readonly CARD_TYPE_STATIC = "staticCard";


}
