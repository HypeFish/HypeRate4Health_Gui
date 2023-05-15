module hyperate.hyperate4health {
  requires javafx.controls;
  requires javafx.fxml;
  requires javafx.web;
  requires javafx.graphics;
  requires javafx.base;
  requires javafx.media;
  requires java.sql;
  requires javax.websocket.api;
  requires org.json;
  requires com.google.gson;
  requires java.desktop;

  exports CLI.model;
  opens CLI.model to javafx.base;
  exports CLI.view;
  opens CLI.view to javafx.fxml;
  opens hyperate.hyperate4health to javafx.fxml;
  exports CLI;
  opens CLI to javafx.fxml;
  exports hyperate.hyperate4health;
  exports CLI.controller;
  opens CLI.controller to javafx.fxml;

}