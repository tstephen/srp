/*******************************************************************************
 * Copyright 2015, 2017 Tim Stephenson and contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
function Charts() {
  this.bar = function(selector, data, options) {
	if (options == undefined) {  
	  options = { 
        margin: {top: 20, right: 20, bottom: 30, left: 50}
	  }
	}
	var width = 400 - options.margin.left - options.margin.right;
    var height = 300 - options.margin.top - options.margin.bottom;
	
	var x = d3.scale.ordinal()
	    .rangeRoundBands([0, width], .1);

	var y = d3.scale.linear()
	    .range([height, 0]);

	var xAxis = d3.svg.axis()
	    .scale(x)
	    .orient("bottom");

	var yAxis = d3.svg.axis()
	    .scale(y)
	    .orient("left")
	    .ticks(10, "%");

	d3.select(selector+' svg').remove();
	
	var svg = d3.select(selector).append("svg")
	    .attr("width", width + options.margin.left + options.margin.right)
	    .attr("height", height + options.margin.top + options.margin.bottom)
	  .append("g")
	    .attr("transform", "translate(" + options.margin.left + "," + options.margin.top + ")");

	  x.domain(data.map(function(d) { return d.barName; }));
	  y.domain([0, d3.max(data, function(d) { return d.barValue; })]);
	
	  svg.append("g")
	      .attr("class", "x axis")
	      .attr("transform", "translate(0," + height + ")")
	      .call(xAxis);
	
	  svg.append("g")
	      .attr("class", "y axis")
	      .call(yAxis)
	    .append("text")
	      .attr("transform", "rotate(-90)")
	      .attr("y", -50)
	      .attr("dy", ".71em")
	      .style("text-anchor", "end")
	      .text(options.yAxisLabel);
	
	  svg.selectAll(".bar")
	      .data(data)
	    .enter().append("rect")
	      .attr("class", "charts bar")
	      .attr("x", function(d) { return x(d.barName); })
	      .attr("width", x.rangeBand())
	      .attr("y", function(d) { return y(d.barValue); })
	      .attr("height", function(d) { return height - y(d.barValue); });
  
  }
  this.donut = function(selector, data, options) {
    if (options == undefined) {  
	  options = { 
        margin: {top: 20, right: 20, bottom: 30, left: 25}
	  }
	}
	var width = 400 - options.margin.left - options.margin.right;
    var height = 300 - options.margin.top - options.margin.bottom;
	var radius = Math.min(width, height) / 2;

	var color = d3.scale.ordinal()
	    .range(["#02857f", "#ec9300", "#0060ad", "#ffffff"]);

	var arc = d3.svg.arc()
	    .outerRadius(radius - 10)
	    .innerRadius(radius - 70);

	var pie = d3.layout.pie()
	    .sort(null)
	    .value(function(d) { return d.value; });

	d3.select(selector+' svg').remove();
	var svg = d3.select(selector).append("svg")
	    .attr("width", width)
	    .attr("height", height)
	  .append("g")
	    .attr("transform", "translate(" + width / 2 + "," + height / 2 + ")");

	var g = svg.selectAll(".arc")
	    .data(pie(data))
	  .enter().append("g")
	    .attr("class", "arc");
	
	g.append("path")
	    .attr("d", arc)
	    .style("fill", function(d) { return color(d.data.label); });
	
	// Arc text
	g.append("text")
	    .attr("transform", function(d) { return "translate(" + arc.centroid(d) + ")"; })
	    .attr("dy", ".35em")
	    .text(function(d) { return d.data.label==undefined ? '' :d.data.label+':'+d.data.value; });
	
	// Central text
	svg.append("text")
		    .attr("transform", "translate(" + -options.margin.left + "," + options.margin.top + ")")
	    .attr("style", "stroke:#333;font-size:3em")
	    .text(d3.sum(data, function(d) { return d.label==undefined ? 0 : d.value; }));
  }
  this.pie = function(selector, data) {
    var w = 500,                        //width
    h = 500,                            //height
    r = 250,                            //radius
    color = d3.scale.category20c();     //builtin range of colors
    
    d3.select(selector+' svg').remove();
    var vis = d3.select(selector)
        .append("svg:svg")              //create the SVG element inside the <body>
        .data([data])                   //associate our data with the document
            .attr("width", w)           //set the width and height of our visualization (these will be attributes of the <svg> tag
            .attr("height", h)
        .append("svg:g")                //make a group to hold our pie chart
            .attr("transform", "translate(" + r + "," + r + ")")    //move the center of the pie chart from 0, 0 to radius, radius

    var arc = d3.svg.arc()              //this will create <path> elements for us using arc data
        .outerRadius(r);
//    console.log('start: '+arc.startAngle()+', end:'+arc.endAngle());
    window.arc = arc; 

    var pie = d3.layout.pie()           //this will create arc data for us given a list of values
        .value(function(d) { return d.value; });    //we must tell it out to access the value of each element in our data array

    var arcs = vis.selectAll("g.slice")     //this selects all <g> elements with class slice (there aren't any yet)
        .data(pie)                          //associate the generated pie data (an array of arcs, each having startAngle, endAngle and value properties) 
        .enter()                            //this will create <g> elements for every "extra" data element that should be associated with a selection. The result is creating a <g> for every object in the data array
            .append("svg:g")                //create a group to hold each slice (we will have a <path> and a <text> element associated with each slice)
                .attr("class", "slice");    //allow us to style things in the slices (like text)

        arcs.append("svg:path")
                .attr("fill", function(d, i) { return color(i); } ) //set the color for each slice to be chosen from the color function defined above
                .attr("d", arc);                                    //this creates the actual SVG path using the associated data (pie) with the arc drawing function

        arcs.append("svg:text")                                     //add a label to each slice
                .attr("transform", function(d) {                    //set the label's origin to the center of the arc
                //we have to make sure to set these before calling arc.centroid
                d.innerRadius = 0;
                d.outerRadius = r;
                console.log('r: '+r);
                //console.log('start: '+arc.startAngle(d)+', end:'+arc.endAngle(d));
                return "translate(" + arc.centroid(d) + ")";        //this gives us a pair of coordinates like [50, 50]
            })
            .attr("text-anchor", "middle")                          //center the text on it's origin
            .text(function(d, i) { return data[i].idx; });        //get the label from our original data array
    }
}