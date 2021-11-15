from behave import *
# from twentyone import *
# backgound
@given(u'a global administrator named "Greg"')
def step_impl(context):
    print('STEP: Given a global administrator named "Greg"')


@given(u'a blog named "Greg\'s anti-tax rants"')
def step_impl(context):
    print('STEP: Given a blog named "Greg\'s anti-tax rants"')


@given(u'a customer named "Wilson"')
def step_impl(context):
    print('STEP: Given a customer named "Wilson"')

# table comming
@given(u'the following people exist')
def step_impl(context):
        for row in context.table:
            print(row)


@given(u'some precondition 1')
def step_impl(context):
    print(u'STEP: Given some precondition 1')


@when(u'some action by the actor')
def step_impl(context):
    print('STEP: When some action by the actor')


@when(u'some other action')
def step_impl(context):
    print('STEP: When some other action')


@then(u'some testable outcome is achieved')
def step_impl(context):
    # print('STEP: Then some testable outcome is achieved')
    assert(1==0)


@then(u'something else we can check happens too')
def step_impl(context):
    # print('STEP: Then something else we can check happens too')
    assert(1==0)

@given(u'something else we can check happens too')
def step_impl(context):
    print('STEP: Given something else we can check happens too')


# scenario 2
@given(u'some precondition')
def step_impl(context):
    print('STEP: Given some precondition')


@given(u'some other precondition with doc string')
def step_impl(context):
    print('STEP: Given some other precondition with doc string')


@when(u'yet another action')
def step_impl(context):
    print('STEP: When yet another action')

# # *
# @given('something else we can check happens too')
# def step_impl(context):
#     raise NotImplementedError(u'STEP: Given something else we can check happens too')


@given(u'I don\'t see something else')
def step_impl(context):
    print('STEP: Given I don\'t see something else')


# scenario 3
@given(u'the cow weighs {weight} kg')
def step_impl(context, weight):
    print('STEP: Given the cow weighs kg: ' + weight)


@when(u'we calculate the feeding requirements')
def step_impl(context):
    print('STEP: When we calculate the feeding requirements')


@then(u'the energy should be {energy} MJ')
def step_impl(context, energy):
    # print('STEP: Then the energy should be MJ: ' + energy)
    assert(1==0)


# @given(u'a blog named "Greg\'s anti-tax rants"')
# def step_impl(context):
#     raise NotImplementedError(u'STEP: Given a blog named "Greg\'s anti-tax rants"')


# @given(u'the cow weighs 500 kg')
# def step_impl(context):
#     raise NotImplementedError(u'STEP: Given the cow weighs 500 kg')
#
#
# @then(u'the energy should be 29500 MJ')
# def step_impl(context):
#     raise NotImplementedError(u'STEP: Then the energy should be 29500 MJ')
