# ScriptLang
Simple scripting dynamic typeless language

Example:
```
//global variable
var globalX=1;

f main(){
    //supports integers
    var x=89;
    //supports strings
    var myString="my string"
    //supports floats
    var myFloat=98.98
    var x,y=myFunction(1,2);
     
    //loops
    for(var i=0;i<10;i=i+1){
    	//do something
    }
    
    //supports conditions
    if(x==3){
        //do something
    }
    
    if(x==4){
    	//do something
    }else{
    	//do something
    }
    
    if(x==1){
        //do something
    }else if(x==2){
        //do something
    }else{
        //do something
    }
    
    //supports tables(or maps)
    var mytable={};
    mytable["key"]="some_value";
    
    //tables can be nested
    var table={};
    table["nestedTable"]={};
    table["nestedTable"]["nestedKey"]="nestedKeyValue";
}

//supports several return values from function
f myFunction(x,y){
    return x+y,x*y
}
```

Also it supports external "platform" functions. 
Example:

Java code:
```
interpreter.getPlatformFunctions().registerFunctionsObject(new Object() {
	@ExternalFunction(name = "testIntSum")
	public long testIntSumFunction(long a, long b) {
		return a + b;
	}
});
```
script:
```
f main(){
	debuglog "resultOfSumFunction" testIntSum(20,5);
}
```
