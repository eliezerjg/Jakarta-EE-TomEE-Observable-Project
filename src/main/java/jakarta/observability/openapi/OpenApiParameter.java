package jakarta.observability.openapi;

import org.eclipse.microprofile.openapi.models.examples.Example;
import org.eclipse.microprofile.openapi.models.media.Content;
import org.eclipse.microprofile.openapi.models.media.Schema;
import org.eclipse.microprofile.openapi.models.parameters.Parameter;

import java.util.Map;

public class OpenApiParameter implements Parameter {
    private String type;
    private String name;

    private In in;

    public OpenApiParameter(String type){
        super();
        this.type = type;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String s) {
        this.name = s;
    }

    @Override
    public In getIn() {
        return this.in;
    }

    @Override
    public void setIn(In in) {
        this.in = in;
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public void setDescription(String s) {

    }

    @Override
    public Boolean getRequired() {
        return null;
    }

    @Override
    public void setRequired(Boolean aBoolean) {

    }

    @Override
    public Boolean getDeprecated() {
        return null;
    }

    @Override
    public void setDeprecated(Boolean aBoolean) {

    }

    @Override
    public Boolean getAllowEmptyValue() {
        return null;
    }

    @Override
    public void setAllowEmptyValue(Boolean aBoolean) {

    }

    @Override
    public Style getStyle() {
        return null;
    }

    @Override
    public void setStyle(Style style) {

    }

    @Override
    public Boolean getExplode() {
        return null;
    }

    @Override
    public void setExplode(Boolean aBoolean) {

    }

    @Override
    public Boolean getAllowReserved() {
        return null;
    }

    @Override
    public void setAllowReserved(Boolean aBoolean) {

    }

    @Override
    public Schema getSchema() {
        return null;
    }

    @Override
    public void setSchema(Schema schema) {

    }

    @Override
    public Map<String, Example> getExamples() {
        return null;
    }

    @Override
    public void setExamples(Map<String, Example> map) {

    }

    @Override
    public Parameter addExample(String s, Example example) {
        return null;
    }

    @Override
    public void removeExample(String s) {

    }

    @Override
    public Object getExample() {
        return null;
    }

    @Override
    public void setExample(Object o) {

    }

    @Override
    public Content getContent() {
        return null;
    }

    @Override
    public void setContent(Content content) {

    }

    @Override
    public Map<String, Object> getExtensions() {
        return null;
    }

    @Override
    public Parameter addExtension(String s, Object o) {
        return null;
    }

    @Override
    public void removeExtension(String s) {

    }

    @Override
    public void setExtensions(Map<String, Object> map) {

    }

    @Override
    public String getRef() {
        return null;
    }

    @Override
    public void setRef(String s) {

    }
}
