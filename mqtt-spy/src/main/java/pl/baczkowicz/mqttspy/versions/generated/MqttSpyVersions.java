//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.7 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.11.26 at 10:19:15 PM GMT 
//


package pl.baczkowicz.mqttspy.versions.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.jvnet.jaxb2_commons.lang.CopyTo;
import org.jvnet.jaxb2_commons.lang.Copyable;
import org.jvnet.jaxb2_commons.lang.Equals;
import org.jvnet.jaxb2_commons.lang.HashCode;
import org.jvnet.jaxb2_commons.lang.ToString;
import org.jvnet.jaxb2_commons.lang.builder.CopyBuilder;
import org.jvnet.jaxb2_commons.lang.builder.JAXBCopyBuilder;
import org.jvnet.jaxb2_commons.lang.builder.JAXBEqualsBuilder;
import org.jvnet.jaxb2_commons.lang.builder.JAXBHashCodeBuilder;
import org.jvnet.jaxb2_commons.lang.builder.JAXBToStringBuilder;


/**
 * <p>Java class for MqttSpyVersions complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="MqttSpyVersions">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ReleaseStatuses" type="{http://baczkowicz.pl/mqtt-spy-versions}ReleaseStatuses"/>
 *         &lt;element name="LatestReleases" type="{http://baczkowicz.pl/mqtt-spy-versions}LatestReleases"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MqttSpyVersions", propOrder = {
    "releaseStatuses",
    "latestReleases"
})
public class MqttSpyVersions
    implements CopyTo, Copyable, Equals, HashCode, ToString
{

    @XmlElement(name = "ReleaseStatuses", required = true)
    protected ReleaseStatuses releaseStatuses;
    @XmlElement(name = "LatestReleases", required = true)
    protected LatestReleases latestReleases;

    /**
     * Gets the value of the releaseStatuses property.
     * 
     * @return
     *     possible object is
     *     {@link ReleaseStatuses }
     *     
     */
    public ReleaseStatuses getReleaseStatuses() {
        return releaseStatuses;
    }

    /**
     * Sets the value of the releaseStatuses property.
     * 
     * @param value
     *     allowed object is
     *     {@link ReleaseStatuses }
     *     
     */
    public void setReleaseStatuses(ReleaseStatuses value) {
        this.releaseStatuses = value;
    }

    /**
     * Gets the value of the latestReleases property.
     * 
     * @return
     *     possible object is
     *     {@link LatestReleases }
     *     
     */
    public LatestReleases getLatestReleases() {
        return latestReleases;
    }

    /**
     * Sets the value of the latestReleases property.
     * 
     * @param value
     *     allowed object is
     *     {@link LatestReleases }
     *     
     */
    public void setLatestReleases(LatestReleases value) {
        this.latestReleases = value;
    }

    public void toString(ToStringBuilder toStringBuilder) {
        {
            ReleaseStatuses theReleaseStatuses;
            theReleaseStatuses = this.getReleaseStatuses();
            toStringBuilder.append("releaseStatuses", theReleaseStatuses);
        }
        {
            LatestReleases theLatestReleases;
            theLatestReleases = this.getLatestReleases();
            toStringBuilder.append("latestReleases", theLatestReleases);
        }
    }

    public String toString() {
        final ToStringBuilder toStringBuilder = new JAXBToStringBuilder(this);
        toString(toStringBuilder);
        return toStringBuilder.toString();
    }

    public void equals(Object object, EqualsBuilder equalsBuilder) {
        if (!(object instanceof MqttSpyVersions)) {
            equalsBuilder.appendSuper(false);
            return ;
        }
        if (this == object) {
            return ;
        }
        final MqttSpyVersions that = ((MqttSpyVersions) object);
        equalsBuilder.append(this.getReleaseStatuses(), that.getReleaseStatuses());
        equalsBuilder.append(this.getLatestReleases(), that.getLatestReleases());
    }

    public boolean equals(Object object) {
        if (!(object instanceof MqttSpyVersions)) {
            return false;
        }
        if (this == object) {
            return true;
        }
        final EqualsBuilder equalsBuilder = new JAXBEqualsBuilder();
        equals(object, equalsBuilder);
        return equalsBuilder.isEquals();
    }

    public void hashCode(HashCodeBuilder hashCodeBuilder) {
        hashCodeBuilder.append(this.getReleaseStatuses());
        hashCodeBuilder.append(this.getLatestReleases());
    }

    public int hashCode() {
        final HashCodeBuilder hashCodeBuilder = new JAXBHashCodeBuilder();
        hashCode(hashCodeBuilder);
        return hashCodeBuilder.toHashCode();
    }

    public Object copyTo(Object target, CopyBuilder copyBuilder) {
        final MqttSpyVersions copy = ((target == null)?((MqttSpyVersions) createCopy()):((MqttSpyVersions) target));
        {
            ReleaseStatuses sourceReleaseStatuses;
            sourceReleaseStatuses = this.getReleaseStatuses();
            ReleaseStatuses copyReleaseStatuses = ((ReleaseStatuses) copyBuilder.copy(sourceReleaseStatuses));
            copy.setReleaseStatuses(copyReleaseStatuses);
        }
        {
            LatestReleases sourceLatestReleases;
            sourceLatestReleases = this.getLatestReleases();
            LatestReleases copyLatestReleases = ((LatestReleases) copyBuilder.copy(sourceLatestReleases));
            copy.setLatestReleases(copyLatestReleases);
        }
        return copy;
    }

    public Object copyTo(Object target) {
        final CopyBuilder copyBuilder = new JAXBCopyBuilder();
        return copyTo(target, copyBuilder);
    }

    public Object createCopy() {
        return new MqttSpyVersions();
    }

}
