package org.grails.twitter.auth

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import org.grails.twitter.PersonController
import org.grails.twitter.PersonService
import org.grails.twitter.Status
import org.grails.twitter.StatusService
import spock.lang.Ignore
import spock.lang.Specification

@TestFor(PersonController)
@Mock([Person, Status, PersonService, StatusService])
class PersonControllerSpec extends Specification {

    def populateValidParams(params) {
        assert params != null

        params["firstName"] = 'TestFirstName'
        params["lastName"] = 'TestLastName'
        params["userName"] = 'testfirstname'
        params["email"] = 'testfirstname@mail.com'
        params["password"] = '123'
        assert true
    }

    void "Test the index action returns the correct model"() {

        when: "The index action is executed"
        controller.index()

        then: "The model is correct"
        !model.personList
        model.personCount == null
    }

    void "Test the create action returns the correct model"() {
        when: "The create action is executed"
        controller.create()

        then: "The model is correctly created"
        model.person != null
    }

    void "Test the save action correctly persists an instance"() {

        when: "The save action is executed with an invalid instance"
        request.contentType = FORM_CONTENT_TYPE
        request.method = 'POST'
        def person = new Person()
        person.validate()
        controller.save(person)

        then: "The create view is rendered again with the correct model"
        model.person != null
        view == 'create'
    }

    void "Test the redirect is issued to the show action"() {

        when: "The save action is executed with a valid instance"
        request.contentType = FORM_CONTENT_TYPE
        request.method = 'POST'
        populateValidParams(params)
        Person person = new Person(params)

        controller.save(person)

        then: "A redirect is issued to the show action"
        response.redirectedUrl == '/person/show/1'
        controller.flash.message != null
        Person.count() == 1
    }

    void "Test that the show action returns the correct model"() {

        when: "A domain instance is passed to the show action"
        populateValidParams(params)
        def person = new Person(params)
        Map map = controller.show(person)

        then: "A model is populated containing the domain instance"
        map.person == person
        map.messages.size() == 0
    }

    @Ignore
    void "Test that the edit action returns the correct model"() {
        when: "The edit action is executed with a null domain"
        controller.edit(null)

        then: "A 404 error is returned"
        response.status == 404

        when: "A domain instance is passed to the edit action"
        populateValidParams(params)
        def person = new Person(params)
        controller.edit(person)

        then: "A model is populated containing the domain instance"
        model.person == person
    }

    @Ignore
    void "Test the update action performs an update on a valid domain instance"() {
        when: "Update is called for a domain instance that doesn't exist"
        request.contentType = FORM_CONTENT_TYPE
        request.method = 'PUT'
        controller.update(null)

        then: "A 404 error is returned"
        response.redirectedUrl == '/person/index'
        flash.message != null

        when: "An invalid domain instance is passed to the update action"
        response.reset()
        def person = new Person()
        person.validate()
        controller.update(person)

        then: "The edit view is rendered again with the invalid instance"
        view == 'edit'
        model.person == person

        when: "A valid domain instance is passed to the update action"
        response.reset()
        populateValidParams(params)
        person = new Person(params).save(flush: true)
        controller.update(person)

        then: "A redirect is issued to the show action"
        person != null
        response.redirectedUrl == "/person/show/$person.id"
        flash.message != null
    }

    @Ignore
    void "Test that the delete action deletes an instance if it exists"() {
        when: "The delete action is called for a null instance"
        request.contentType = FORM_CONTENT_TYPE
        request.method = 'DELETE'
        controller.delete(null)

        then: "A 404 is returned"
        response.redirectedUrl == '/person/index'
        flash.message != null

        when: "A domain instance is created"
        response.reset()
        populateValidParams(params)
        def person = new Person(params).save(flush: true)

        then: "It exists"
        Person.count() == 1

        when: "The domain instance is passed to the delete action"
        controller.delete(person)

        then: "The instance is deleted"
        Person.count() == 0
        response.redirectedUrl == '/person/index'
        flash.message != null
    }
}