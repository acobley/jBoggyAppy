var NumPosts=1;

var NumSubscriptions=0;
var lsACall=0;
var loadsaCall = [0];
var lpACall=0;


var pAuthor="_All-Authors_"; // The Posts the author made
var sAuthor="_All-Authors_"; // The authors subscriptions
var PostorTag="Post";

$(function(){
	ShowScrollingTags();
	
	var AddSomething=$("#AddMessageForm");
	AddSomething.append($("<input>").attr("type","Submit").attr("value","speak").click(function()
			{
			
		var Author=$("#AuthorName").text();
		var thisTitle = $("#SayWhat").val();
//		alert("Click"+ Author+"Title "+ thisTitle);
		$.post("/jBloggyAppy/Article" , $("#AddMessageForm").serialize());
		var t=setTimeout(loadArticle,1000,thisTitle,Author);
		
		return false;
		}));
	var int;
	
	var ChangeList=$("#changeaAuthorListForm");
	ChangeList.append($("<input>").attr("type","Submit").attr("ID","PostsButton").attr("value","All Posts").click(function()
			{
		var pButton=$("#PostsButton");	
		if (pAuthor!="_All-Authors_"){
			PostorTag="Post";
			pAuthor="_All-Authors_";
			pButton.attr("value","My Posts");
		}
		else{
			PostorTag="Post";
			pAuthor=$("#AuthorName").text();
			pButton.attr("value","All Posts");
		}
		var t=setTimeout(loadAuthor,50,pAuthor,sAuthor);
		
		return false;
		}));
	
    self = this;
	 pAuthor=$("h1 #AuthorName").text();
	
	if (pAuthor.length<5){
		pAuthor="_All-Authors_";
	}
	sAuthor=pAuthor;
	loadAuthor(pAuthor,sAuthor);
	
	var t=setTimeout(ScrollNow,500);
	
	 
});

function ScrollNow(){
	$("ul#ticker01").liScroll();
}
function setHash(path)
{
	if (currentLocation != path)
	{
		currentLocation = path;
		window.location.hash = path;
	}
}
var currentLocation = "";






function clearNav()
{
	$("#nav").empty();
}

function clearBlogs()
{
	$("#Blogs").empty();
}

function shownavWaiting()
{
	$("#nav").hide();
	$("#navloading").show(200);
}

function hidenavWaiting()
{
	$("#nav").show(200);
	$("#navloading").hide(200);
}

function clearContent()
{
	$("#content").empty();
}

function showWaiting()
{
	$("#content").hide();
	$("#loading").show(200);
}

function hideWaiting()
{
	$("#content").show(200);
	$("#loading").hide(200);
}

function setTitle(str){
	//$("#title").text(str);
}

function ShowScrollingTags(){
	ticker = $("#ticker01");
	
	
	$.get("/jBloggyAppy/Tags/json", function(data)
		{
		data = data["Data"];
		for(var i in data)
			{ 
			var tag = data[i]["tag"];
			ticker.append($("<li id='scroll"+i+"'></li>"));
			Scroll=$("#scroll"+i);
			//Scroll.append("<span>10/10/2007</span>");
			Scroll.append(
						$("<a></a>").attr("href", tag).text(tag).click(function()
						{
							var thisTitle = $(this).attr("href");
							pAuthor= thisTitle;
							PostorTag="Tag";
							loadPosts(thisTitle);
							//loadTaggedPosts(thisTitle);
							return false;
						})
					);
			}
		}, "json");
	
}



function loadAuthor(pAuthor,sAuthor)
{
	
	//alert("Load Authors"+Author);
	loadPosts(pAuthor);
	loadSubscriptions(sAuthor);
}



function loadPosts(Author)
{
	var int;
    self = this;
	if (lpACall==0){
		lpACall=1;
		//alert ("lpACall "+lpACall+Author);
		int=self.setInterval(loadPosts,10000,Author);
	}
	$.get("/jBloggyAppy/"+PostorTag+"/"+pAuthor+"/json", function(data)
	{
		//hidenavWaiting();
		//setTitle("Post");
		//alert(Author);
		data = data["Data"];
		//alert (" NumPosts "+NumPosts+" Data length"+data.length);
		if (data.length != NumPosts){
			NumPosts=data.length;
			clearBlogs();
			$('#ArticleHeader').empty();
			$("#ArticleHeader").append("<h2>Messages</h2>");
			$("#ArticleHeader").append("<ul></ul>");
			for(var i in data)
			{ 
				var title = data[i]["title"]
				//alert(title);                   
				$("#ArticleHeader ul").append(
					$("<li></li>").append(
						$("<a></a>").attr("href", title).text(title).click(function()
						{
							var thisTitle = $(this).attr("href");
						
							loadArticle(thisTitle,Author);
							return false;
						})
					)
				);
			}
		}
	}, "json");
}


function loadSubscriptions(Author){
	var int;
    self = this;
	if (lsACall==0){
		lsACall=1;
		int=self.setInterval(loadSubscriptions,10000,Author);
	}
	
	
	$.get("/jBloggyAppy/Subscribe/"+Author+"/json", function(data)
			{	
				data = data["Data"];
				//alert("Subscriptions"+data+"Author "+Author+" Length "+data.length);
				if (data.length != NumSubscriptions){
					
					$('#tags').empty();
					$("#UserTags").empty();
					
					$("#SubscribedTags").empty();
					$("#SubscribedArticles").empty();
					$("#UserTags").append("<h3>Your Subscriptions</h3>");
					NumSubscriptions=data.length;
					
					for(var i in data)
					{
						var tag = data[i]["tag"];
						$("#SubscribedArticles").append($("<h4></h4>").text(tag));
						$("#SubscribedArticles").append($("<div></div>").attr("id",tag));
						loadSubscribedArticles(i,tag,Author);
					}
				}
			}, "json");
}

function loadSubscribedArticles(i,tag,Author){
	var int;
    self = this;
	 //console.log("called with i=" + i);
	if (typeof loadsaCall === "undefined" || typeof loadsaCall[i]=== "undefined" || loadsaCall[i] === 0)  {
		loadsaCall[i]=1;
		
		int=self.setInterval(loadSubscribedArticles,10000,i,tag,Author);
		
	}
	
	$.get("/jBloggyAppy/Tag/"+tag+"/json", function(data)
			{
				
				
				data = data["Data"];
				$("#"+tag).empty();
				$("#"+tag).append("<ul></ul>");
				
				
				for(var i in data)
				{
					var title = data[i]["title"];
					$("#"+tag+" ul").append(
						//$("<li></li>").text(title)
							
						//);
						
					$("<li></li>").append(
							$("<a></a>").attr("href", title).text(title).click(function()
								{
								var thisTitle = $(this).attr("href");
								
								loadArticle(thisTitle,Author);
								return false;
								})
						)
						);
					
				}
				
			}, "json");
}

function loadArticle(title,Author)
{
	
	clearContent();
	showWaiting();
	
	$.get("/jBloggyAppy/Article/"+ title + "/json", function(data)
	{
		hideWaiting();
		$("#content").append("<h2>"+title+"</h2>")
		$("#content").append("<table></table>")
		
		var table = $("#content table");
		table.append($("<tr><td id='AuthorPostName'></td><td> Says:</tr>"));
		table.append($("<tr><td colspan='2'>" + data["body"] + "</td></tr>"));
		table.append($("<tr><td>On: </td><td>" + data["pubDate"] + "</td></tr>"));
		
		$("#AuthorPostName").append($("<a></a>").attr("href", data["author"]).text(data["author"]).click(function()
				{
			
				pAuthor=$("#AuthorPostName").text();
				
		
			var t=setTimeout(loadAuthor,50,pAuthor,sAuthor);
			
			return false;
			
				
				}));
		var Tags=data["tags"]
		var SplitTags = Tags.split(",")
		
		table.append($("<tr><td>Tags (click to follow):</td>"))
		for(i = 0; i < SplitTags.length; i++){
			var TagLink="/jBloggyAppy/Subscribe/"+SplitTags[i];
			table.append($("<td></td").attr("id","TagsList"))
			$("#TagsList").append($("<a></a>").attr("href", TagLink).text(SplitTags[i]).click(function()
							{
						var thisTag = $(this).attr("text");
						subscribe(thisTag,Author);
						return false;
					}));
					
			
			//table.append($("</td>"))
			//table.append($("<td><a href=/jBloggyAppy/Subscribe/" + SplitTags[i] + "]</td>"))
		}
		
		//table.append($("</tr>"))
		
		$("#content").append("<article></article>");
		var article = $("#content article");
		article.append($("<article> </article>").attr("id","Comments"));
		loadComment(Author,title);
		
		article.append($("<form ><form>").attr("id","CommentForm"));
		var form=$("#content form");
		form.append($("<input></input>").attr("type","hidden").attr("name","Author").attr("ID","AuthorComment").attr("value",Author));
		form.append($("<input></input>").attr("type","hidden").attr("name","Title").attr("ID","TitleComment").attr("value",title));

		form.append($("<textarea></textarea>").attr("name","Comment").attr("rows","2").attr("cols",10).attr("id","CommentBox"));
		form.append($("<input>").attr("type","Submit").attr("value","AddComment").click(function()
				{
			var Author=$("#AuthorComment").val();
			var thisTitle = $("#TitleComment").val();
			
			$.post("/jBloggyAppy/Comment" , $("#CommentForm").serialize());
			loadArticle(thisTitle,Author);
			return false;
			}));
		
		
	}, "json");
}



function subscribe(tag,Author){
	$.post("/jBloggyAppy/Subscribe" , {Tag:""+tag+""});
	loadSubscriptions(Author);
}

function loadComment(Author,title){
	var int;
    self = this;
   
    /*
	if (lsACall==0){
		lsACall=1;
		int=self.setInterval(loadSubscriptions,10000,Author);
	}
	*/
    $("#Comments").empty();
	$.get("/jBloggyAppy/Comment/"+title+"/json", function(data)
			{	
				data = data["Data"];
				//alert("Subscriptions"+data+"Author "+Author+" Length "+data.length);
				//if (data.length != NumSubscriptions){
					//NumSubscriptions=data.length;
				$("#Comments").append("<table></table>").attr("class","commentstable");
					var Comments=$("#Comments");
					for(var i in data)
					{
						//alert("Data length"+data.length +"  "+data[i]["body"]+ data[i]["author"]);
						//$("#Comments").append("<h3>"+title+"</h3>")
						
						var table = $("#Comments table");
						//table.append($("<tr></tr>").attr("id","C"));
						table.append($("<tr><td>" + data[i]["author"] + "</td></tr>"))
						table.append($("<tr><td>" + data[i]["body"] + "</td></tr>"))
						table.append($("<tr><td>" + data[i]["pubDate"] + "</td></tr>"))
						
						Comments.append($("</p></p>").attr("id",i));
						//var Paragraph=$("<p> "+i);
						//Paragraph.text("test");
						//		.append( ""+ data[i]["author"]));
						//Comments.append($("</p></p>").append( ""+ data[i]["body"]));
						//Comments.append($("</p></p>").append( ""+ data[i]["pubDate"]));

						
						
					}
				//}
			}, "json");
}


/*!
 * liScroll 1.0
 * Examples and documentation at: 
 * http://www.gcmingati.net/wordpress/wp-content/lab/jquery/newsticker/jq-liscroll/scrollanimate.html
 * 2007-2010 Gian Carlo Mingati
 * Version: 1.0.2 (30-MARCH-2009)
 * Dual licensed under the MIT and GPL licenses:
 * http://www.opensource.org/licenses/mit-license.php
 * http://www.gnu.org/licenses/gpl.html
 * Requires:
 * jQuery v1.2.x or later
 * 
 */


jQuery.fn.liScroll = function(settings) {
		settings = jQuery.extend({
		travelocity: 0.07
		}, settings);		
		return this.each(function(){
				var $strip = jQuery(this);
				$strip.addClass("newsticker")
				var stripWidth = 0;
				var $mask = $strip.wrap("<div class='mask'></div>");
				var $tickercontainer = $strip.parent().wrap("<div class='tickercontainer'></div>");								
				var containerWidth = $strip.parent().parent().width();	//a.k.a. 'mask' width 	
				$strip.find("li").each(function(i){
				stripWidth += jQuery(this, i).outerWidth(true); // thanks to Michael Haszprunar
				});
				$strip.width(stripWidth);			
				var totalTravel = stripWidth+containerWidth;
				var defTiming = totalTravel/settings.travelocity;	// thanks to Scott Waye		
				function scrollnews(spazio, tempo){
				$strip.animate({left: '-='+ spazio}, tempo, "linear", function(){$strip.css("left", containerWidth); scrollnews(totalTravel, defTiming);});
				}
				scrollnews(totalTravel, defTiming);				
				$strip.hover(function(){
				jQuery(this).stop();
				},
				function(){
				var offset = jQuery(this).offset();
				var residualSpace = offset.left + stripWidth;
				var residualTime = residualSpace/settings.travelocity;
				scrollnews(residualSpace, residualTime);
				});			
		});	
};
