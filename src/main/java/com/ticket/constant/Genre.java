package com.ticket.constant;

public enum Genre {
   CLASSIC("서양음악(클래식)"),
   CIRCUSMAGIC("서커스/마술"),
   THEATER("연극"),
   KOREANTRADITIONAL("한국음악(국악)"),
   COMPOSITE("복합"),
   MUSICAL("뮤지컬"),
   DANSING("무용"),
   PUBLICMUSIC("대중음악"),
   PUBLICDANCE("대중무용");
   
   private final String displayName;
   
   Genre(String displayName) {
      this.displayName = displayName;
   }
   
   public String getDisplayName() {
      return displayName;
   }
}

