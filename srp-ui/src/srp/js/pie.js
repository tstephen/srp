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
function renderPie(selector, csvString, options) { // jshint ignore:line
  var defaultOptions = {
    colors: ["#0B4BE5", "#0072CE", "#0BBDE5", "#0BDBC9", "#00F299", "#A2FC00", "#FFEB00", "#FFAD00"],
    labels: true,
    other: 5
  };
  options = $.extend(defaultOptions, options == undefined ? {} : options);

  // If csvString missing or only header row without data
  if (csvString == undefined || csvString.trim().length==0 || csvString.trim().split('\n').length<2) {
    csvString = 'classification,percentage\nNo data,100 \n';
  }

  var svg = d3.select(selector),
      width = +svg.attr("width"),
      height = +svg.attr("height"),
      radius = Math.min(width, height) / 2,
      g = svg.append("g").attr("transform", "translate(" + width / 2 + "," + height / 2 + ")");

  var color = d3.scaleOrdinal(options.colors);

  var pie = d3.pie()
      .sort(null)
      .value(function(d) { return d.percentage; });

  var path = d3.arc()
      .outerRadius(radius - 10)
      .innerRadius(0);

  var label = d3.arc()
      .outerRadius(radius - 40)
      .innerRadius(radius - 40);

  var other = 0;
  var data = d3.csvParse(csvString, function(d) {
    if (d.percentage > options.other) {
      d.percentage = +d.percentage;
      return d;
    } else {
      other+=parseFloat(d.percentage);
      return;
    }
  });
  if (other > 0) data.push({ classification: 'Other', percentage: other });

  var arc = g.selectAll(".arc")
    .data(pie(data))
    .enter().append("g")
      .attr("class", "arc");

  arc.append("path")
      .attr("d", path)
      .attr('class', function(d) { return d.data.classification.toLowerCase(); })
      .attr("fill", function(d) { return color(d.data.classification); });

  if (options.labels) {
    arc.append("text")
        .attr("transform", function(d) { return "translate(" + label.centroid(d) + ")"; })
        .attr("dy", "0.35em")
        .text(function(d) { return d.data.classification; });
  }
}
