# Ti.SpinMenu

This is the Appcelerator Titanium version of [SpinMenu](https://github.com/Hitomis/SpinMenu)

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