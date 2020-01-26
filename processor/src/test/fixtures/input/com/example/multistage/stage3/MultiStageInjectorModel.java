package com.example.multistage.stage3;

import com.example.multistage.stage1.Model1;
import com.example.multistage.stage1.Model2;
import com.example.multistage.stage1.MyFragment;
import com.example.multistage.stage2.Model3;
import sting.Injector;

@Injector( includes = { Model1.class, MyFragment.class } )
interface MultiStageInjectorModel
{
  // From MyFragment
  Model2 getModel2();

  // This dependency should be auto-added by virtue of being an @Injectable
  Model3 getModel3();
}
