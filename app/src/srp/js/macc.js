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
var macc = new Macc(); 

function Macc() {
  this.data; 

  this.options = { 
    margin: {top: 10, right: 100, bottom: 10, left: 75},
    minHeight: 768,
    scaleFunc: d3.scale.linear
  };
  
  this.removeCurrentGraph = function(selector) { 
    $(selector).fadeOut(1000, function() { $(selector).empty(); });
  };

  /**
   * @param selector Where to attach the graph
   * @param data Interventions data to display in graph
   */
  this.displayDataSet = function(selector, data) { 
    console.log('displaying macc for '+ data.length+' interventions');
    
    if (window.innerWidth>1280) {
      macc.options.width = window.innerWidth - macc.options.margin.left*2 - macc.options.margin.right*2;
    } else {
      macc.options.width = window.innerWidth - macc.options.margin.left - macc.options.margin.right;
    }
    macc.options.height = window.innerHeight - macc.options.margin.top - macc.options.margin.bottom +100;
    if (macc.options.height <= macc.options.minHeight) macc.options.height = macc.options.minHeight;
    
    $(selector).empty();
    var svg = d3.select(selector)
      .append('svg')
      .attr('width',macc.options.width)
      .attr('height',macc.options.height); 
//    svg.append('<defs> \
//        <filter id="dropGlow" width="1.5" height="1.5" x="-.25" y="-.25"> \
//            <feGaussianBlur id="feGaussianBlur5384" in="SourceAlpha" stdDeviation="15.000000" result="blur"/> \
//            <feColorMatrix id="feColorMatrix5386" result="bluralpha" type="matrix" values="-1 0 0 0 1 0 -1 0 0 1 0 0 -1 0 1 0 0 0 0.800000 0 "/> \
//            <feOffset id="feOffset5388" in="bluralpha" dx="0.000000" dy="0.000000" result="offsetBlur"/> \
//            <feMerge id="feMerge5390"> \
//                <feMergeNode id="feMergeNode5392" in="offsetBlur"/> \
//                <feMergeNode id="feMergeNode5394" in="SourceGraphic"/> \
//            </feMerge> \
//        </filter>\
//    </defs>');
    
    // set up scales
    var sumTonnesCo2eSavedTargetYear = d3.sum(data, function(d,i){
      return d.tonnesCo2eSavedTargetYear;
    });
    var xScale = d3.scale.linear()
      .domain([0,sumTonnesCo2eSavedTargetYear])
      .range([macc.options.margin.left,macc.options.width]);
    macc.yExtent = d3.extent(data, function(d,i) {
      return d.costPerTonneCo2e;
    });
    if (macc.yExtent[0]<macc.options.yNegLimit) macc.yExtent[0] = macc.options.yNegLimit;
    // The +10 avoids cutting the topmost axis label in half
    if (macc.yExtent[1]>macc.options.yPosLimit) macc.yExtent[1] = macc.options.yPosLimit+10;
    if (macc.yExtent[1]<0) macc.yExtent[1] = 10;
    macc.yScale = macc.options.scaleFunc()
      .domain(macc.yExtent)
      .range([macc.options.height-macc.options.margin.top-macc.options.margin.bottom,0]);
    macc.options.breakevenAxis = macc.yScale(0);
    
    svg.selectAll('rect')
        .data(data).enter().append('rect')
        //TODO this transitions from top left not grow away from x axis 
        //.transition()
        .attr({
          id: function(d) {
            return d.slug+'Rect';
          },
          class:'bar',
          y: function(d){
            if (d.costPerTonneCo2e>=0){
//              console.log('scaled y as: '+macc.options.breakevenAxis-yScale(d.costPerTonneCo2e)+','+yScale(d.costPerTonneCo2e)+' when cost per tonne ='+d.costPerTonneCo2e);
              return macc.yScale(d.costPerTonneCo2e);
            } else {
//              console.log('Cost of '+d.name+' is '+d.costPerTonneCo2e);
              return macc.options.breakevenAxis;
            }
          },
          height: function(d) {
            if (d.costPerTonneCo2e>=0){
              //console.log('scaled y as: '+macc.options.breakevenAxis-yScale(d.costPerTonneCo2e)+','+yScale(d.costPerTonneCo2e)+' when cost per tonne ='+d.costPerTonneCo2e);
              return macc.options.breakevenAxis-macc.yScale(d.costPerTonneCo2e);
            } else {
              return macc.yScale(d.costPerTonneCo2e)-macc.options.breakevenAxis;
            }
          },
          x: function(d,i) {
            x = 0;
            $.each(data,function(j,d) {
              if (j<i) {
                x+=d.tonnesCo2eSavedTargetYear;
              }
            });
            //console.log('for x '+x+', scaled x ='+xScale(x));
            return Math.round(xScale(x)); 
          },
          width: function(d) {
            return xScale(d.tonnesCo2eSavedTargetYear)-macc.options.margin.left;
          },
          fill: function(d) {
            var color = Math.round(50+d.confidence/100*255);
            //console.log('color depth:'+color);
            if (d.costPerTonneCo2e>=0){
              return 'rgb('+color+',0,0)';
            } else {
              return 'rgb(0,'+color+',0)';
            }
          }
        })
        .on('click', function(d) {
          console.log('click on '+d.slug);
          $('[data-type]').removeClass('selected');
          $('[data-type="'+d.slug+'"]').addClass('selected');
          document.querySelector('[data-type="'+d.slug+'"]').scrollIntoView();
        })
        .on('mouseover', function(d) {
          //console.log('Show tooltip');
          d3.select('.macc-tooltip').classed('hidden',false);
          // NOTE: will return px even if set in %, but ONLY when visible
          var ttWidth = $('.macc-tooltip').css('width');
          if (ttWidth.indexOf('px')!=-1) ttWidth = parseFloat(ttWidth.substring(0,ttWidth.indexOf('px'))); 
//          console.error('  ttWidth:'+ttWidth);
          var xPos = d3.mouse(this)[0];
          // Position tooltip to left when it would otherwise be off page.
          if ((xPos+ttWidth)>macc.options.width) {
//            console.log('  tooltip on left '+(xPos+ttWidth)+','+macc.options.width);
            xPos = xPos+parseFloat(d3.select(this).attr('width'))-ttWidth;
          } else {
//            console.log('  tooltip on right '+(xPos+ttWidth)+','+macc.options.width);
          }

          var overlapAdj = ractive.get('overlapCount')==undefined ? 0 :ractive.get('overlapCount');
          var yPos = d3.mouse(this)[1]+50+(overlapAdj*20);
//          console.log('  x,y:'+xPos+','+yPos);
          d3.select('.macc-tooltip')
            .style({
              'left':xPos+'px',
              'top':yPos+'px'
            });          
          
          d3.select('.macc-tooltip #name').text(' '+d.name);
          d3.select('.macc-tooltip #cost').text(
              ' £ ' + d.costPerTonneCo2e.formatDecimal(0, '.', ','));
          d3.select('.macc-tooltip #savings').text(' '+d.tonnesCo2eSavedTargetYear.formatDecimal(0, '.', ',')+' tonnes');
          d3.select('.macc-tooltip #confidence').text(' '+d.confidence+' %');
          d3.select('#'+this.id).classed('selected',true);
        })
        .on("mouseout", function(d) {
          //console.log('Hide tooltip');
          d3.select(".macc-tooltip").classed("hidden", true);
          d3.select('#'+this.id).classed('selected',false);
        });

    // x axis label 
    svg.append('text')
        .attr({
          x:macc.options.width-300,
          y:macc.options.breakevenAxis+35,
          class:'axis-label'
        })
        .text('tonnes of CO2e saved in '+macc.options.targetYear);
  
    // y axis label 
    svg.append('text')
      .attr({
        x:0,
        y:100,
        class:'axis-label'
      })
      .attr('transform','rotate(-90),translate(-200,-80)')
      .text('cost per tonne CO2e saved');

    // set up x axis
    var xAxis = d3.svg.axis().scale(xScale).orient('bottom');
    svg.append('g').classed('axis',true)
      .attr('transform','translate(0,'+macc.options.breakevenAxis+')').call(xAxis);
    
    // set up y axis
    var yAxis = d3.svg.axis().scale(macc.yScale).orient('left');
    svg.append('g').classed('axis',true)
      .attr('transform','translate('+macc.options.margin.left+',0)').call(yAxis);
  }
}

function initHeader() {
  console.info('ready...');
  $('#resultTable').on('mouseover', function() {
    console.log('hello');
    $('#resultTable thead').css('position','fixed').css('top','10px').css('width',$('#resultTable').width());
  });
  $('#resultTable').on('mouseout', function() {
    console.log('bye');
    $('#resultTable thead').css('position','static').css('top','10px').css('width',$('#resultTable').width());
  });
}
