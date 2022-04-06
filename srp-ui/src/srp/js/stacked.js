/*******************************************************************************
 * Copyright 2015-2020 Tim Stephenson and contributors
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
 ******************************************************************************/
function renderStacked(selector, csvString, options) { // jshint ignore:line
  var defaultOptions = {
    colors: ["#0B4BE5", "#0072CE", "#0BBDE5", "#0BDBC9", "#00F299", "#A2FC00", "#FFEB00", "#FFAD00"],
    legendWidth: 300,
    margin: {top: 20, right: 10, bottom: 50, left: 40},
    xAxisLabel: "Financial Years",
    yAxisLabel: "Tonnes CO\u2082e"
  };
  options = $.extend(defaultOptions, options == undefined ? {} : options);

  // first clean up
  d3.select(selector+' g').remove();
  var svg = d3.select(selector),
      margin = options.margin,
      width = +svg.attr("width") - margin.left - margin.right,
      chartWidth = width - options.legendWidth,
      height = +svg.attr("height") - margin.top - margin.bottom,
      g = svg.append("g").attr("transform", "translate(" + margin.left + "," + margin.top + ")");

  var x = d3.scaleBand()
      .rangeRound([0, chartWidth])
      .paddingInner(0.05)
      .align(0.1);

  var y = d3.scaleLinear()
      .rangeRound([height, 0]);

  var z = d3.scaleOrdinal()
      .range(options.colors);

  var data = d3.csvParse(csvString, function(d, i, columns) {
    d.total = 0;
    for (i = 1; i < columns.length; ++i) d.total += d[columns[i]] = +d[columns[i]];
    return d;
  });

  var keys = data.columns.slice(1);

  //data.sort(function(a, b) { return b.total - a.total; });
  x.domain(data.map(function(d) { return d.Period; }));
  y.domain([0, d3.max(data, function(d) { return d.total; })]).nice();
  // y.domain([0, 0.05]);
  z.domain(keys);

  g.append("g")
    .selectAll("g")
    .data(d3.stack().keys(keys)(data))
    .enter().append("g")
      .attr("fill", function(d) { return z(d.key); })
    .selectAll("rect")
    .data(function(d) { return d; })
    .enter().append("rect")
      .attr("x", function(d) { return x(d.data.Period); })
      .attr("y", function(d) { return y(d[1]); })
      .attr("height", function(d) { return y(d[0]) - y(d[1]); })
      .attr("width", x.bandwidth());

  g.append("g")
      .attr("class", "axis")
      .attr("transform", "translate(0," + height + ")")
      .call(d3.axisBottom(x))
    .append("text")
      .attr("x", width-options.legendWidth)
      .attr("y", y(y.ticks().pop()) + 0.5)
      .attr("dy", "2em")
      .attr("fill", "#000")
      .attr("font-weight", "bold")
      .attr("text-anchor", "start")
      .text(options.xAxisLabel);

  g.selectAll(".tick>text")
    .style("text-anchor","end")
    .attr("transform", function() {
      return "rotate(-45)";
    });

  g.append("g")
      .attr("class", "axis")
      .call(d3.axisLeft(y).ticks(null, "s"))
    .append("text")
      .attr("x", 2)
      .attr("y", y(y.ticks().pop()) + 0.5)
      .attr("dy", "-0.5em")
      .attr("fill", "#000")
      .attr("font-weight", "bold")
      .attr("text-anchor", "start")
      .text(options.yAxisLabel);

  var legend = g.append("g")
      .attr("text-anchor", "end")
    .selectAll("g")
    .data(keys.slice().reverse())
    .enter().append("g")
      .attr("transform", function(d, i) { return "translate(0," + i * 20 + ")"; });

      legend.append("rect")
          .attr("x", width - 19)
          .attr("width", 19)
          .attr("height", 19)
          .attr("fill", z);

      legend.append("text")
          .attr("x", width - 24)
          .attr("y", 9.5)
          .attr("dy", "0.32em")
          .text(function(d) { return d; });

  // SDU target lines
  if (data[0]!=undefined && data[0]['Core emissions'] != undefined) {
    // Climate Change Act 2008 target line
    // Interpreted in the NHS as
    // - a 10% reduction between 2007-08 and 2015-16 ; and
    // - an overall 34% reduction by 2020/21
    // Only to be applied to the core emissions.
    var x1 = 0;
    var y1 = data[0]['Core emissions']; // Core emissions in 2007-08 is 100% (baseline)
    var x2 = x('2015-16')+( x.bandwidth()/2); // x dimension scaled to 2015-16
    var y2 = y1 * (1 - ((100-90) / 100)); // 10% reduction from 2007-08 baseline
    var ccaPhase1 = [[x1,y1,x2,y2]];
    var x3 = x('2020-21'); // x dimension scaled to 2020
    if (x3 == undefined) x3 = chartWidth;
    var y3 = y2 * (1 - ((100-66   ) / 100)); // 34% reduction from 2015-16 to 20-21
    var ccaPhase2 = [[x2,y2,x3,y3]];

    g.selectAll(".ccaPhase1")
        .data(ccaPhase1)
        .enter()
      .append("line")
        .attr("class", "ccaPhase1")
        .attr("x1", function(d) { return d[0]; })
        .attr("y1", function(d) { return y(d[1]); })
        .attr("x2", function(d) { return d[2]; })
        .attr("y2", function(d) { return y(d[3]); })
        .attr("stroke", "black")
        .attr("stroke-width", 1);

    g.selectAll(".ccaPhase2")
        .data(ccaPhase2)
        .enter()
      .append("line")
        .attr("class", "ccaPhase2")
        .attr("x1", function(d) { return d[0]; })
        .attr("y1", function(d) { return y(d[1]); })
        .attr("x2", function(d) { return d[2]; })
        .attr("y2", function(d) { return y(d[3]); })
        .attr("stroke", "black")
        .attr("stroke-width", 1);


    var trendLines = ["CCA 2008"]; // just in case don't have enough org data

    // Organisation's own target - n% reduction in emissions from base year by 2020/21
    /*jshint -W069 */
    if (data[4]!=undefined && data[4]['total'] != undefined &&
        data[4]['total'] > 0) {
      trendLines = ["CCA 2008", "Org'n Target"]; // both trend lines available

      var n = 32;
      var x4 = x('2011-12'); // x dimension scaled to baseline year
      var y4 = data[4]['total']; //
      var x5 = x('2020-21'); // x dimension scaled to 2020
      if (x5 == undefined) x5 = chartWidth;
      var y5 = y4 * (1 - ((n) / 100)); // n% reduction from baseline
      var orgTarget = [[x4,y4,x5,y5]];

      g.selectAll(".orgTarget")
          .data(orgTarget)
          .enter()
        .append("line")
          .attr("class", "orgTarget")
          .attr("x1", function(d) { return d[0]; })
          .attr("y1", function(d) { return y(d[1]); })
          .attr("x2", function(d) { return d[2]; })
          .attr("y2", function(d) { return y(d[3]); })
          .attr("stroke", "#0B4BE5")
          .attr("stroke-width", 1);
    }
    /*jshint +W069 */

    var legend2 = g.append("g")
        .attr("text-anchor", "end")
      .selectAll("g")
      .data(trendLines)
      .enter().append("g")
        .attr("transform", function(d, i) { return "translate(0," + (i+5) * 20 + ")"; });

    var z2 = d3.scaleOrdinal().range(['#000',"#0B4BE5"]);
    legend2.append("rect")
        .attr("x", width - 19)
        .attr("width", 19)
        .attr("height", 3)
        .attr("fill", z2);

    legend2.append("text")
        .attr("x", width - 24)
        .attr("y", 9.5)
        .attr("dy", "-0.2em")
        .text(function(d) { return d; });
  }
}
