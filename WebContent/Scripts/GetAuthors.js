$(function(){
	loadAuthors();
	
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
	$("#title").text(str);
}

function loadAuthors()
{
	
	clearNav();
	shownavWaiting();
	setTitle("jBloggyAppy")
	$.get("/jBloggyAppy/Author/json", function(data)
	{
		hidenavWaiting();
		setTitle("Authors");
		
		data = data["Data"];
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
	}, "json");
	
}

function loadAuthor(Author)
{
	
	clearContent();
	showWaiting();
	$.get("/jBloggyAppy/Author/"+ Author + "/json", function(data)
	{
		hideWaiting();
		
		$("#content").append("<h1>Details: "+Author+"</h1>");
		$("#content").append("<table></table>");
		
		var table = $("#content table");
		table.append($("<tr><td>Name</td><td>" + data["name"] + "</td></tr>"))
		table.append($("<tr><td>Email</td><td>" + data["emailName"] + "</td></tr>"))
		table.append($("<tr><td>Twitter</td><td>" + data["twitterName"] + "</td></tr>"))
		table.append($("<tr><td>address</td><td>" + data["address"] + "</td></tr>"))
	}, "json");
	loadPosts(Author);
}

function loadPosts(Author)
{
	clearBlogs();
	
	
	$.get("/jBloggyAppy/Post/"+Author+"/json", function(data)
	{
		hidenavWaiting();
		setTitle("Post");
		
		data = data["Data"];
		$("#Blogs").append("<h1>Stories</h1");
		$("#Blogs").append("<ul></ul>");
		for(var i in data)
		{
			var title = data[i]["title"]
			$("#Blogs ul").append(
				$("<li></li>").append(
					$("<a></a>").attr("href", title).text(title).click(function()
						{
						var thisTitle = $(this).attr("href");
						loadArticle(thisTitle);
						return false;
						})
				)
			)
		}
	}, "json");
}

function loadArticle(title)
{
	
	clearContent();
	showWaiting();
	
	$.get("/jBloggyAppy/Article/"+ title + "/json", function(data)
	{
		hideWaiting();
		$("#content").append("<h1>"+title+"</h1")
		$("#content").append("<table></table>")
		
		var table = $("#content table");
		table.append($("<tr><td>Author</td><td>" + data["author"] + "</td></tr>"))
		table.append($("<tr><td>Body</td><td>" + data["body"] + "</td></tr>"))
		table.append($("<tr><td>Date</td><td>" + data["pubDate"] + "</td></tr>"))
		table.append($("<tr><td>Author</td><td>" + data["author"] + "</td></tr>"))
	}, "json");
}

