$(function(){
	loadAuthors();
	var int=self.setInterval("loadAuthors()",10000);
	 
});

function setHash(path)
{
	if (currentLocation != path)
	{
		currentLocation = path;
		window.location.hash = path;
	}
}
var currentLocation = "";


setInterval(100, function()
{
	if (currentLocation != window.location.hash)
	{
		//do something
	}
})



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

var NumAuthors=0;

function loadAuthors()
{
	
	
	//shownavWaiting();
	//setTitle("jBloggyAppy")
	
	$.get("/jBloggyAppy/Author/json", function(data)
	{
		//hidenavWaiting();
		setTitle("Authors");
		
		data = data["Data"];
		//alert("NumAuthors"+data.length);
		if (data.length != NumAuthors){
			clearNav();
			NumAuthors=data.length;
			$("#nav").append("<ul></ul>");
		
			for(var i in data)
			{
			
				var title = data[i]["name"];
			                    
				$("#nav ul").append(
						$("<li></li>").append(
								$("<a></a>").attr("href", title).text(title).click(function()
							{
									var thisTitle = $(this).attr("href");
									loadAuthor(thisTitle);
									return false;
							})
						)
				);
			}
		}
	}, "json");
	
}



function loadAuthor(Author)
{
	$("#Subscriptions").empty();
	$("#Subscriptions").append("<h2> Subscriptions for "+Author+"</h2>");
	clearContent();
	showWaiting();
	$.get("/jBloggyAppy/Author/"+ Author + "/json", function(data)
	{
		hideWaiting();
		
		$("#content").append("<h2>Details: "+Author+"</h2>");
		$("#content").append("<table></table>");
		
		var table = $("#content table");
		table.append($("<tr><td>Name:</td><td>" + data["name"] + "</td></tr>"))
		table.append($("<tr><td>Email:</td><td>" + data["emailName"] + "</td></tr>"))
		table.append($("<tr><td>Twitter:</td><td>" + data["twitterName"] + "</td></tr>"))
		table.append($("<tr><td>Address:</td><td>" + data["address"] + "</td></tr>"))
	}, "json");
	loadPosts(Author);
	loadSubscriptions(Author);
}

var NumPosts=0;
var lpACall=0;
function loadPosts(Author)
{
	if (lpACall==0){
		lpAcall=1;
		var int=self.setInterval("loadPosts('"+Author+"')",1000);
	}
	$.get("/jBloggyAppy/Post/"+Author+"/json", function(data)
	{
		//hidenavWaiting();
		setTitle("Post");
		
		data = data["Data"];
		if (data.length != NumPosts){
			NumPosts=data.length;
			clearBlogs();
			$("#Blogs").append("<h2>Stories</h2>");
			$("#Blogs").append("<ul></ul>");
			for(var i in data)
			{ 
				var title = data[i]["title"]
				$("#Blogs ul").append(
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

var NumSubscriptions=0;
var lsACall=0;
function loadSubscriptions(Author){
	if (lsACall==0){
		lsACall=1;
		var int=self.setInterval("loadSubscriptions(Author)",1000);
	}
	$("#SubscribedTags").empty();
	$("#SubscribedArticles").empty();
	$.get("/jBloggyAppy/Subscribe/"+Author+"/json", function(data)
			{
				hidenavWaiting();
				setTitle("Post");
				
				data = data["Data"];
				if (data.length != NumSubscriptions){
					NumSubscriptions=data.length;
					$("#SubscribedTags").append("<ul></ul>");
					for(var i in data)
					{
						var tag = data[i]["tag"];
						$("#SubscribedTags ul").append(
						$("<li></li>").text(tag)
							
						);
						loadSubscribedArticles(tag,Author);
					}
				}
			}, "json");
}

function loadSubscribedArticles(tag,Author){
	
	$("#SubscribedArticles").append($("<h2></h2>").text(tag));
	$("#SubscribedArticles").append($("<div></div>").attr("id",tag));
	$.get("/jBloggyAppy/Tag/"+tag+"/json", function(data)
			{
				hidenavWaiting();
				setTitle("Post");
				
				data = data["Data"];
				
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
		table.append($("<tr><td>Author: </td><td>" + data["author"] + "</td></tr>"))
		table.append($("<tr><td>Body: </td><td>" + data["body"] + "</td></tr>"))
		table.append($("<tr><td>Date: </td><td>" + data["pubDate"] + "</td></tr>"))
		var Tags=data["tags"]
		var SplitTags = Tags.split(",")
		
		table.append($("<tr><td>Tags:</td>"))
		for(i = 0; i < SplitTags.length; i++){
			var TagLink="/jBloggyAppy/Subscribe/"+SplitTags[i];
			table.append($("<td>"))
			table.append($("<a></a>").attr("href", TagLink).text(SplitTags[i]).click(function()
							{
						var thisTag = $(this).attr("text");
						subscribe(thisTag,Author);
						return false;
					}))
					
			
			table.append($("</td>"))
			//table.append($("<td><a href=/jBloggyAppy/Subscribe/" + SplitTags[i] + "]</td>"))
		}
		table.append($("</tr>"))
	}, "json");
}

function subscribe(tag,Author){
	$.post("/jBloggyAppy/Subscribe" , {Tag:""+tag+""});
	loadSubscriptions(Author);
}

