package com.cz2002.hrps.controls;

import com.cz2002.hrps.boundaries.InputBoundary;
import com.cz2002.hrps.entities.Guest;
import com.cz2002.hrps.models.Menu;
import com.cz2002.hrps.models.MenuOption;
import com.cz2002.hrps.models.PromptModel;

public class GuestController extends EntityController {

  @Override
  public void index() {
    InputBoundary inputBoundary = new InputBoundary(new PromptModel(
      "",
      new Menu(
        "Guest Menu",
        new MenuOption[] {
          new MenuOption("update_guest", "Update"),
          new MenuOption("search_guest", "Search"),
          new MenuOption("back", "Back"),
        }
      )
    ));

    int menuSelection = 0;
    do {
      menuSelection = inputBoundary.processMenu(true).getValue();
      switch (menuSelection) {
        case 1:
          update(new Guest());
          break;
        case 2:
          findList(new Guest());
          break;
        default:
          break;
      }
    } while (menuSelection != 3);
  }

}
