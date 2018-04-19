package org.rikarika.pqs.ps4.view;

import org.rikarika.pqs.ps4.model.FourInARowListener;
import org.rikarika.pqs.ps4.model.FourInARowModel;

/**
 * FourInARowView is an agent that stands between one or more local or remote players and
 * FourInARowModel.
 * 
 * @author Jiadong Zhu
 *
 */
public abstract class FourInARowView implements FourInARowListener {

  protected FourInARowModel model;

  public FourInARowView(FourInARowModel model) {
    this.model = model;
    this.model.addEventListener(this);
  }


}
