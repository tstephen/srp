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
package digital.srp.finance.model.converters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.GregorianCalendar;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import digital.srp.finance.model.FinancialTransaction;
import digital.srp.finance.model.LineType;
import digital.srp.finance.model.OrderType;
import digital.srp.finance.model.UnitType;

public class FinancialTransactionCsvParserTest {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @Test
    public void testParse() {
        String fieldNames = "poNum,orderType,supplierName,reqNo,orderDate,deliverTo,contract,lineType,line,shipment,distribution,description,supplierItem,uom,qtyOrdered,qtyReceived,qtyInvoiced,valInvoiced,qtyCandidate,unitPrice,currency,amount,buyer,needByDate,costCentre,account,chargeAccount,account (again),accountDesc,chargeAccountDesc,status,EClass,EClassCategory,requisitionPreparer,month,trust,cag,serviceLine,costCentreDescr,group";
        String line = "40063574,Standard Purchase Order,MEDICAL EQUIPMENT SUPPLIES LTD,60071016,09-Apr-13,AN9205,,Service,2,1,1,Please top up PO 40063574 to pay outstanding invoices for the quarterly services of autoclaves and ultrasonic scalers for the mobile dental services.,N/A,Each, 230 , -   , -   , -   , -   , 1.00 , GBP , 230.00 ,\"Haque, Ahsanul\",22-May-13,G15054,544200,RNJN.G15054.544200.000000.000000,544200,CLEANING EQUIPMENT,RNJ Exchequer Funds.SALARIED DENTAL SERVICE.CLEANING EQUIPMENT.DEFAULT.DEFAULT,Approved,M.MF.MFD,Sterilisers Autoclaves Desinfectors & Accessories,\"Kapora, Faith\",1,BARTS,SURGERY & CANCER,DENTAL & OMFS,SALARIED DENTAL SERVICE,Hotel Services Equipment Materials & Services";

        FinancialTransactionCsvParser parser = new FinancialTransactionCsvParser();
        List<String> parsedFieldNames = parser.parseFieldNames(fieldNames);
        assertNotNull(parsedFieldNames);
        assertEquals(40, parsedFieldNames.size());
        FinancialTransaction bean = (FinancialTransaction) parser.parseLine(
                line, parsedFieldNames);
        assertNotNull(bean);
        assertEquals((Long) 40063574l, bean.getPoNum());
        assertEquals(OrderType.STANDARD, bean.getOrderType());
        assertEquals("MEDICAL EQUIPMENT SUPPLIES LTD", bean.getSupplierName());
        assertEquals(OrderType.STANDARD, bean.getOrderType());
        assertEquals((Long) 60071016l, bean.getReqNo());
        assertEquals(new GregorianCalendar(2013, 3, 9).getTime(),
                bean.getOrderDate());
        assertEquals("AN9205", bean.getDeliverTo());
        assertNull(bean.getContract());
        assertEquals(LineType.SERVICE, bean.getLineType());
        assertEquals(new Short("2"), bean.getLine());
        assertEquals(new Short("1"), bean.getShipment());
        assertEquals(new Short("1"), bean.getDistribution());
        assertEquals(
                "Please top up PO 40063574 to pay outstanding invoices for the quarterly services of autoclaves and ultrasonic scalers for the mobile dental services.",
                bean.getDescription());
        assertEquals(UnitType.EACH, bean.getUom());
        assertEquals((Float) 230.0f, bean.getAmount());
    }

}
