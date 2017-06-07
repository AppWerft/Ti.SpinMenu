# Ti.SpinMenu

This is the Appcelerator Titanium version of [SpinMenu](https://github.com/Hitomis/SpinMenu)

The project is WIP. If you "only" want to realize a rotary wheel menu (only images, no sub views) you can look to [Ti.FortuneWheelView](https://github.com/AppWerft/Ti.FortuneWheelView/). This module is ready to use.

![](https://github.com/Hitomis/SpinMenu/raw/master/preview/menu_cyclic.gif) ![](https://github.com/Hitomis/SpinMenu/raw/master/preview/menu_slop.gif)


## Constants

no constants

## Usage

```javascript
var SpinMenuView = require("ti.spinmenu").createSpinMenu({
	views : ["red","blue","orange","green","black"].map(function(color){
		return Ti.UI.createView({
			backgroundColor : color, 
			width : 200,
			height : 400
		});
	})
});
SpinMenuView.addEventListener("spinselected",onSpinSelectedFn);
```
